# QLT-0006: Error de escala 10x en rednessRatio Multi-Toma e inconsistencia entre isAnemic y severity

- **Estado:** Resuelto
- **Severidad:** 🔴 Blocker
- **Regla Sonar:** `kotlin:S2184` (Precisión y coherencia aritmética) · Cualidad: Reliability
- **Ubicación:** `app/src/main/java/com/anemia/detection/engine/AnemiaInferenceEngine.kt:167-168`, `app/src/main/java/com/anemia/detection/engine/AnemiaInferenceEngine.kt:236-237`, `app/src/main/java/com/anemia/detection/engine/AnemiaInferenceEngine.kt:256`
- **Fecha detección:** 2026-10-16

## Descripción
Se identificaron dos defectos de cálculo en `AnemiaInferenceEngine`:
1. **Factor de escala erróneo (10x) en `rednessRatio` durante `analyzeMultiShot` (línea 256):** Se dividía por `10.0` en lugar de `100.0`.
2. **Contradicción de estado entre `isAnemic` y `severity` (líneas 167-168):** Cuando `anemiaProb >= optimalThreshold` con Hb limítrofe, `isAnemic` era `true` pero `severity` permanecía en `NORMAL`.

## Plan de remediación (micro-pasos)
- [x] **Paso 1:** Corregir el divisor de `rednessRatio` en `AnemiaInferenceEngine.kt:256` a `/ 100.0`.
- [x] **Paso 2:** Alinear la coherencia entre `isAnemic` y `severity` en `analyze()` y `analyzeMultiShot()` promoviendo a `AnemiaSeverity.MILD` cuando el umbral Youden detecta anemia limítrofe con Hb en el borde del corte OMS.

## Bitácora
- **2026-10-16 Detectado en auditoría exhaustiva:** Registrado como `Pendiente`.
- **2026-10-16 Pasos 1 y 2 aplicados:** Se corrigió `(avgRedness * 100.0).toInt() / 100.0` en `analyzeMultiShot()` y se garantizó que `severity` sea al menos `AnemiaSeverity.MILD` siempre que `isAnemic` sea `true`. Hallazgo resuelto.
