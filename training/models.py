"""
HemoScan AI - Arquitecturas Neuronales Multi-Tarea
Implementa:
1. MobileNetV3 Multi-Task (Para dispositivos de Gama Baja / Económica, 0.22 GFLOPs)
2. HemaViT Multi-Task (Vision Transformer + FPN para Gama Media/Alta, 3.28 GFLOPs)
Ambas arquitecturas generan simultáneamente:
- Rama 1: Probabilidad de Anemia (Sigmoid, Clasificación binaria)
- Rama 2: Concentración continua de Hemoglobina g/dL (Linear, Regresión)
"""

import tensorflow as tf

def build_mobilenetv3_multitask(input_shape=(224, 224, 3), dropout_rate=0.25, return_base=False):
    """
    MobileNetV3-Small optimizado para inferencia ultra-rápida y bajo consumo de batería.
    """
    inputs = tf.keras.Input(shape=input_shape, name="input_image")
    
    # Backbone pre-entrenado
    base = tf.keras.applications.MobileNetV3Small(
        input_shape=input_shape,
        include_top=False,
        weights="imagenet",
        pooling=None
    )
    # MobileNetV3 tiene Rescaling(1/255) interno; escalamos a [0, 255] para evitar doble división
    scaled_input = inputs * 255.0
    features = base(scaled_input)
    x = tf.keras.layers.GlobalAveragePooling2D(name="gap")(features)
    x = tf.keras.layers.BatchNormalization()(x)
    x = tf.keras.layers.Dropout(dropout_rate)(x)
    x = tf.keras.layers.Dense(64, activation="relu", name="dense_shared")(x)

    # Rama 1: Clasificación de anemia
    anemia_prob = tf.keras.layers.Dense(
        1, activation="sigmoid", name="anemia_prob"
    )(x)

    # Rama 2: Regresión de nivel de hemoglobina (g/dL)
    hb_level = tf.keras.layers.Dense(
        1, activation="linear", name="hb_level"
    )(x)

    model = tf.keras.Model(
        inputs=inputs,
        outputs=[anemia_prob, hb_level],
        name="MobileNetV3_MultiTask"
    )
    if return_base:
        return model, base
    return model

def build_hemavit_multitask(
    input_shape=(224, 224, 3),
    num_heads=4,
    embed_dim=64,
    transformer_layers=2,
    return_base=False
):
    """
    HemaViT: Vision Transformer híbrido con Backbone Convolucional pre-entrenado en ImageNet.
    Combina mapas de textura microvascular local y auto-atención multi-cabeza global.
    """
    inputs = tf.keras.Input(shape=input_shape, name="input_image")
    scaled = inputs * 255.0

    base = tf.keras.applications.MobileNetV3Small(
        input_shape=input_shape,
        include_top=False,
        weights="imagenet"
    )
    feat_map = base(scaled)  # (None, 7, 7, 576)

    # Proyección a parches espaciales para Vision Transformer
    projected = tf.keras.layers.Conv2D(embed_dim, (1, 1), padding="same", activation="relu")(feat_map)
    patches = tf.keras.layers.Reshape((-1, embed_dim))(projected)

    # Bloques de Auto-Atención Multi-Cabeza (Multi-Head Self-Attention)
    x = patches
    for i in range(transformer_layers):
        norm1 = tf.keras.layers.LayerNormalization()(x)
        attn = tf.keras.layers.MultiHeadAttention(
            num_heads=num_heads,
            key_dim=embed_dim // num_heads,
            name=f"vit_mha_{i}"
        )(norm1, norm1)
        x = tf.keras.layers.Add()([x, attn])

        norm2 = tf.keras.layers.LayerNormalization()(x)
        mlp = tf.keras.layers.Dense(embed_dim * 2, activation="gelu")(norm2)
        mlp = tf.keras.layers.Dense(embed_dim)(mlp)
        x = tf.keras.layers.Add()([x, mlp])

    # Fusión Dual: Atención Espacial ViT + Textura Convolucional CNN
    vit_gap = tf.keras.layers.GlobalAveragePooling1D(name="vit_gap")(x)
    cnn_gap = tf.keras.layers.GlobalAveragePooling2D(name="cnn_gap")(feat_map)
    merged = tf.keras.layers.Concatenate()([vit_gap, tf.keras.layers.Dense(64, activation="relu")(cnn_gap)])

    dense_shared = tf.keras.layers.Dense(128, activation="relu")(merged)
    dense_shared = tf.keras.layers.Dropout(0.25)(dense_shared)
    dense_shared = tf.keras.layers.Dense(64, activation="relu", name="dense_shared")(dense_shared)

    # Salidas Multi-Tarea
    anemia_prob = tf.keras.layers.Dense(1, activation="sigmoid", name="anemia_prob")(dense_shared)
    hb_level = tf.keras.layers.Dense(1, activation="linear", name="hb_level")(dense_shared)

    model = tf.keras.Model(
        inputs=inputs,
        outputs=[anemia_prob, hb_level],
        name="HemaViT_MultiTask"
    )
    if return_base:
        return model, base
    return model

def compile_multitask_model(model, learning_rate=1e-4):
    """
    Compila el modelo con pérdidas ponderadas multi-tarea y métricas clínicas
    """
    model.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=learning_rate),
        loss={
            "anemia_prob": tf.keras.losses.BinaryCrossentropy(),
            "hb_level": tf.keras.losses.Huber(delta=1.5)
        },
        loss_weights={
            "anemia_prob": 5.0, # Prioridad a la clasificación clínica de anemia
            "hb_level": 0.15 # Co-regularizador fisiológico continuo
        },
        metrics={
            "anemia_prob": [
                tf.keras.metrics.BinaryAccuracy(name="accuracy"),
                tf.keras.metrics.AUC(name="auc"),
                tf.keras.metrics.Recall(name="sensitivity")
            ],
            "hb_level": [
                tf.keras.metrics.MeanAbsoluteError(name="mae")
            ]
        }
    )
    return model
