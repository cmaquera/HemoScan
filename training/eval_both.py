"""
HemoScan AI - Evaluación de Modelos TFLite Actualizados
Evalúa hemavit_anemia_quant.tflite y mobilenetv3_anemia_quant.tflite
"""
import os
import csv
import numpy as np
import tensorflow as tf
from PIL import Image
from sklearn.metrics import (
    accuracy_score, roc_auc_score, confusion_matrix,
    recall_score, precision_score, f1_score, mean_absolute_error, roc_curve
)
from scipy.stats import pearsonr

DATASET_ROOT = os.path.join(os.path.dirname(os.path.dirname(__file__)), "dataset")
METADATA_FILE = os.path.join(DATASET_ROOT, "metadata_unified.csv")

def evaluate_single_model(model_name):
    model_path = os.path.join(os.path.dirname(os.path.dirname(__file__)), "app", "src", "main", "assets", "models", model_name)
    print("=" * 80)
    print(f"EVALUANDO MODELO: {model_name}")
    print(f"Ruta: {model_path}")
    print(f"Tamaño en disco: {os.path.getsize(model_path) / 1024:.2f} KB ({os.path.getsize(model_path) / (1024*1024):.2f} MB)")
    print("=" * 80)

    interpreter = tf.lite.Interpreter(model_path=model_path)
    interpreter.allocate_tensors()

    in_details = interpreter.get_input_details()
    out_details = interpreter.get_output_details()

    print(f"Firma Entrada: {in_details[0]['shape'].tolist()} ({in_details[0]['dtype'].__name__})")
    print(f"Firma Salida: {[d['shape'].tolist() for d in out_details]} ({out_details[0]['dtype'].__name__})")

    records = []
    with open(METADATA_FILE, "r", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            records.append(row)

    y_true_clf = []
    y_pred_prob = []
    y_true_hb = []
    y_pred_hb = []
    valid = 0

    for r in records:
        img_abs = os.path.join(DATASET_ROOT, r["image_path"])
        if not os.path.exists(img_abs):
            continue

        try:
            img = Image.open(img_abs).convert("RGB").resize((224, 224))
            arr = np.array(img, dtype=np.float32) / 255.0
            input_tensor = np.expand_dims(arr, axis=0)

            interpreter.set_tensor(in_details[0]["index"], input_tensor)
            interpreter.invoke()

            if len(out_details) == 1 and out_details[0]["shape"][-1] == 2:
                out = interpreter.get_tensor(out_details[0]["index"])
                prob = float(out[0][0])
                hb = float(out[0][1])
            elif len(out_details) >= 2:
                prob = float(interpreter.get_tensor(out_details[0]["index"]).flatten()[0])
                hb = float(interpreter.get_tensor(out_details[1]["index"]).flatten()[0])
            else:
                out = interpreter.get_tensor(out_details[0]["index"])
                prob = float(out.flatten()[0])
                hb = 12.0

            y_true_clf.append(int(r["is_anemic"]))
            y_pred_prob.append(prob)
            y_true_hb.append(float(r["hb_level"]))
            y_pred_hb.append(hb)
            valid += 1
        except Exception as e:
            pass

    y_true_clf = np.array(y_true_clf)
    y_pred_prob = np.array(y_pred_prob)
    y_true_hb = np.array(y_true_hb)
    y_pred_hb = np.array(y_pred_hb)

    # AUC
    try:
        auc = roc_auc_score(y_true_clf, y_pred_prob)
    except:
        auc = 0.5

    # Threshold 0.5
    preds_05 = (y_pred_prob >= 0.50).astype(int)
    acc_05 = accuracy_score(y_true_clf, preds_05)
    sens_05 = recall_score(y_true_clf, preds_05, zero_division=0)
    cm_05 = confusion_matrix(y_true_clf, preds_05)
    tn_05, fp_05, fn_05, tp_05 = cm_05.ravel()
    spec_05 = tn_05 / (tn_05 + fp_05) if (tn_05 + fp_05) > 0 else 0
    prec_05 = precision_score(y_true_clf, preds_05, zero_division=0)
    f1_05 = f1_score(y_true_clf, preds_05, zero_division=0)

    # Youden optimal threshold
    fpr, tpr, thresholds = roc_curve(y_true_clf, y_pred_prob)
    youden_idx = np.argmax(tpr - fpr)
    opt_thresh = float(thresholds[youden_idx])
    if opt_thresh > 1.0 or opt_thresh < 0.0:
        opt_thresh = 0.50

    preds_opt = (y_pred_prob >= opt_thresh).astype(int)
    acc_opt = accuracy_score(y_true_clf, preds_opt)
    sens_opt = recall_score(y_true_clf, preds_opt, zero_division=0)
    cm_opt = confusion_matrix(y_true_clf, preds_opt)
    tn_o, fp_o, fn_o, tp_o = cm_opt.ravel()
    spec_opt = tn_o / (tn_o + fp_o) if (tn_o + fp_o) > 0 else 0
    prec_opt = precision_score(y_true_clf, preds_opt, zero_division=0)
    f1_opt = f1_score(y_true_clf, preds_opt, zero_division=0)

    mae_hb = mean_absolute_error(y_true_hb, y_pred_hb)
    try:
        r_corr, _ = pearsonr(y_true_hb, y_pred_hb)
    except:
        r_corr = 0.0

    print(f"Muestras evaluadas: {valid}")
    print(f"AUC-ROC:                                   {auc * 100:.2f}%")
    print(f"Error Hb (MAE):                            {mae_hb:.2f} g/dL (Pearson r = {r_corr:.3f})")
    print(f"--- Métricas con Umbral Estándar (0.50) ---")
    print(f"  * Exactitud (Accuracy):                 {acc_05 * 100:.2f}%")
    print(f"  * Sensibilidad (Recall - Anemia):       {sens_05 * 100:.2f}%")
    print(f"  * Especificidad (Sanos):                {spec_05 * 100:.2f}%")
    print(f"  * Precisión:                            {prec_05 * 100:.2f}%")
    print(f"  * F1-Score:                             {f1_05 * 100:.2f}%")
    print(f"  * Matriz: TP={tp_05}, TN={tn_05}, FP={fp_05}, FN={fn_05}")
    print(f"--- Métricas con Umbral Óptimo Youden ({opt_thresh:.3f}) ---")
    print(f"  * Exactitud (Accuracy):                 {acc_opt * 100:.2f}%")
    print(f"  * Sensibilidad (Recall - Anemia):       {sens_opt * 100:.2f}%")
    print(f"  * Especificidad (Sanos):                {spec_opt * 100:.2f}%")
    print(f"  * Precisión:                            {prec_opt * 100:.2f}%")
    print(f"  * F1-Score:                             {f1_opt * 100:.2f}%")
    print(f"  * Matriz: TP={tp_o}, TN={tn_o}, FP={fp_o}, FN={fn_o}")
    print("=" * 80 + "\n")

    return {
        "model": model_name,
        "auc": auc,
        "acc_05": acc_05,
        "sens_05": sens_05,
        "spec_05": spec_05,
        "acc_opt": acc_opt,
        "sens_opt": sens_opt,
        "spec_opt": spec_opt,
        "opt_thresh": opt_thresh,
        "mae_hb": mae_hb,
        "r_corr": r_corr
    }

if __name__ == "__main__":
    res_vit = evaluate_single_model("hemavit_anemia_quant.tflite")
    res_mob = evaluate_single_model("mobilenetv3_anemia_quant.tflite")
