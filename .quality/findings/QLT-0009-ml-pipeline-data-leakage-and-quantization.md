# QLT-0009: Data Leakage en evaluación ML, partición no agrupada por paciente y generador PTQ desconectado

- **Estado:** Resuelto
- **Severidad:** 🟠 High
- **Regla Sonar:** `python:S1481` (Variables/funciones no conectadas y rigor de validación ML) · Cualidad: Reliability
- **Ubicación:** `training/evaluate_tflite_metrics.py:40-55`, `training/train.py:70-76`, `training/export_tflite.py:21-46`
- **Fecha detección:** 2026-10-16

## Descripción
En el pipeline de Machine Learning (`training/`) existen tres problemas metodológicos:
1. **Evaluación sobre datos de entrenamiento (`evaluate_tflite_metrics.py:40-55`):** Evalúa los modelos `.tflite` sobre la totalidad de `metadata_unified.csv` (899 registros), mezclando el 80% usado en entrenamiento con el 20% de validación.
2. **Partición aleatoria por imagen en vez de por paciente (`train.py:70-76`):** `np.random.permutation(len(records))` divide imágenes individuales sin agrupar por paciente ni estratificar por clase, causando fuga de datos cuando un mismo paciente tiene múltiples fotografías.
3. **Generador de calibración no conectado (`export_tflite.py:21-46`):** La función `representative_data_gen()` está definida pero nunca se asigna a `converter.representative_dataset`.

## Plan de remediación (micro-pasos)
- [x] **Paso 1:** Conectar `representative_data_gen` (usando muestras reales del dataset cuando existan) en `training/export_tflite.py`.
- [x] **Paso 2:** Particionar de forma agrupada por paciente (`split_records_by_patient`) en `training/train.py` y filtrar al conjunto de validación hold-out en `training/evaluate_tflite_metrics.py`.

## Bitácora
- **2026-10-16 Detectado en auditoría exhaustiva:** Registrado como `Pendiente`.
- **2026-10-16 Remediado:** Conectado `converter.representative_dataset = representative_data_gen` con muestras reales en `export_tflite.py`, implementada partición agrupada por paciente (`split_records_by_patient`) en `train.py` y restringida la evaluación clínica en `evaluate_tflite_metrics.py` a la partición hold-out de validación. Estado actualizado a `Resuelto`.
