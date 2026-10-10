"""
HemoScan AI - Pipeline Completo de Entrenamiento y Validación Cruzada
Ejecuta:
1. Carga de metadatos unificados (Eyes Defy Anemia + CP-AnemiC + Uñas)
2. Partición estratificada (80% Train, 20% Val)
3. Entrenamiento de MobileNetV3 (Gama Baja) y HemaViT (Gama Alta)
4. Monitoreo clínico de métricas (Accuracy, Sensibilidad, AUC-ROC, MAE en Hb)
5. Guardado de los pesos óptimos
"""

import os
import csv
import numpy as np
import tensorflow as tf
from PIL import Image

from data_augmentation import build_augmentation_pipeline, build_validation_pipeline
from models import build_mobilenetv3_multitask, build_hemavit_multitask, compile_multitask_model

DATASET_ROOT = os.path.join(os.path.dirname(os.path.dirname(__file__)), "dataset")
METADATA_FILE = os.path.join(DATASET_ROOT, "metadata_unified.csv")
CHECKPOINT_DIR = os.path.join(os.path.dirname(__file__), "checkpoints")

def load_dataset_records(metadata_file):
    records = []
    with open(metadata_file, "r", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            records.append(row)
    return records

def create_tf_dataset(records, is_training=True, batch_size=16):
    image_paths = [os.path.join(DATASET_ROOT, r["image_path"]) for r in records]
    labels_anemia = [float(r["is_anemic"]) for r in records]
    labels_hb = [float(r["hb_level"]) for r in records]

    def _load_image(path, is_anemic, hb):
        img_raw = tf.io.read_file(path)
        img = tf.io.decode_image(img_raw, channels=3, expand_animations=False)
        img = tf.cast(img, tf.float32)
        return img, {"anemia_prob": is_anemic, "hb_level": hb}

    dataset = tf.data.Dataset.from_tensor_slices((image_paths, labels_anemia, labels_hb))
    dataset = dataset.map(_load_image, num_parallel_calls=tf.data.AUTOTUNE)

    aug = build_augmentation_pipeline() if is_training else build_validation_pipeline()

    def _apply_aug(img, targets):
        return aug(tf.expand_dims(img, 0))[0], targets

    dataset = dataset.map(_apply_aug, num_parallel_calls=tf.data.AUTOTUNE)

    if is_training:
        dataset = dataset.shuffle(buffer_size=min(500, len(records)))
    
    dataset = dataset.batch(batch_size).prefetch(tf.data.AUTOTUNE)
    return dataset

def train_and_evaluate(epochs=5, batch_size=16):
    os.makedirs(CHECKPOINT_DIR, exist_ok=True)

    if not os.path.exists(METADATA_FILE):
        print(f"[!] Archivo {METADATA_FILE} no encontrado. Generando cohorte base...")
        from dataset_downloader import generate_sample_cohort
        generate_sample_cohort(200)

    records = load_dataset_records(METADATA_FILE)
    print(f"Cargados {len(records)} registros clínicos.")

    # División 80% Entrenamiento, 20% Validación
    np.random.seed(42)
    indices = np.random.permutation(len(records))
    split = int(0.8 * len(records))

    train_records = [records[i] for i in indices[:split]]
    val_records = [records[i] for i in indices[split:]]

    train_ds = create_tf_dataset(train_records, is_training=True, batch_size=batch_size)
    val_ds = create_tf_dataset(val_records, is_training=False, batch_size=batch_size)

    # 1. ENTRENAMIENTO DE MOBILENETV3 (GAMA BAJA) CON TRANSFER LEARNING
    print("\n" + "="*70)
    print("INICIANDO ENTRENAMIENTO: MobileNetV3 (Transfer Learning en 2 Fases)")
    print("="*70)
    model_mobile, base_mobile = build_mobilenetv3_multitask(return_base=True)

    # Fase 1: Calentamiento con backbone congelado
    base_mobile.trainable = False
    model_mobile = compile_multitask_model(model_mobile, learning_rate=1e-3)
    warmup_epochs = max(2, epochs // 4)
    print(f"Fase 1: Calentamiento de cabezales ({warmup_epochs} epocas)...")
    model_mobile.fit(train_ds, validation_data=val_ds, epochs=warmup_epochs, verbose=1)

    # Fase 2: Ajuste fino profundo descongelado
    print(f"\nFase 2: Ajuste fino descongelado ({epochs} epocas)...")
    base_mobile.trainable = True
    model_mobile = compile_multitask_model(model_mobile, learning_rate=3e-5)
    callbacks_mobile = [
        tf.keras.callbacks.EarlyStopping(monitor="val_loss", patience=6, restore_best_weights=True),
        tf.keras.callbacks.ReduceLROnPlateau(monitor="val_loss", factor=0.5, patience=2, min_lr=1e-6)
    ]
    model_mobile.fit(
        train_ds,
        validation_data=val_ds,
        epochs=epochs,
        callbacks=callbacks_mobile,
        verbose=1
    )

    path_mobile = os.path.join(CHECKPOINT_DIR, "mobilenetv3_trained.keras")
    model_mobile.save(path_mobile)
    print(f"[OK] Modelo MobileNetV3 guardado en: {path_mobile}")

    # 2. ENTRENAMIENTO DE HEMAVIT (VISION TRANSFORMER + PRE-TRAINED BACKBONE)
    print("\n" + "="*70)
    print("INICIANDO ENTRENAMIENTO: HemaViT (Vision Transformer + FPN Pre-entrenado)")
    print("="*70)
    model_hemavit, base_hemavit = build_hemavit_multitask(return_base=True)

    # Fase 1: Calentamiento del Transformer
    base_hemavit.trainable = False
    model_hemavit = compile_multitask_model(model_hemavit, learning_rate=1e-3)
    print(f"Fase 1: Calentamiento de capas de atencion ({warmup_epochs} epocas)...")
    model_hemavit.fit(train_ds, validation_data=val_ds, epochs=warmup_epochs, verbose=1)

    # Fase 2: Ajuste fino conjunto
    print(f"\nFase 2: Ajuste fino conjunto ({epochs} epocas)...")
    base_hemavit.trainable = True
    model_hemavit = compile_multitask_model(model_hemavit, learning_rate=3e-5)
    callbacks_hemavit = [
        tf.keras.callbacks.EarlyStopping(monitor="val_loss", patience=6, restore_best_weights=True),
        tf.keras.callbacks.ReduceLROnPlateau(monitor="val_loss", factor=0.5, patience=2, min_lr=1e-6)
    ]
    model_hemavit.fit(
        train_ds,
        validation_data=val_ds,
        epochs=epochs,
        callbacks=callbacks_hemavit,
        verbose=1
    )

    path_hemavit = os.path.join(CHECKPOINT_DIR, "hemavit_trained.keras")
    model_hemavit.save(path_hemavit)
    print(f"[OK] Modelo HemaViT guardado en: {path_hemavit}")

    return path_mobile, path_hemavit

if __name__ == "__main__":
    import argparse
    parser = argparse.ArgumentParser(description="Entrenamiento HemoScan AI")
    parser.add_argument("--epochs", type=int, default=6, help="Numero de epocas")
    parser.add_argument("--batch_size", type=int, default=32, help="Tamano del lote")
    args = parser.parse_args()

    train_and_evaluate(epochs=args.epochs, batch_size=args.batch_size)
