# QLT-0008: Recomendaciones dinámicas ignoradas en ResultScreen y valor ficticio en HomeScreen sin historial

- **Estado:** Resuelto
- **Severidad:** 🟠 High
- **Regla Sonar:** `kotlin:S1128` / Coherencia de datos en UI · Cualidad: Reliability / Maintainability
- **Ubicación:** `app/src/main/java/com/anemia/detection/ui/screens/ResultScreen.kt:357-384`, `app/src/main/java/com/anemia/detection/ui/screens/HomeScreen.kt:421-427`
- **Fecha detección:** 2026-10-16

## Descripción
1. **Recomendaciones ignoradas en `ResultScreen` (`ResultScreen.kt:357-384`):**
   `AnemiaInferenceEngine` genera `result.recommendations` específicas según `AnemiaSeverity` (incluyendo alertas urgentes para `SEVERE` y la nota estadística del protocolo Multi-Toma con el error estándar `±SE` y el conteo de outliers descartados en `AnemiaInferenceEngine.kt:239-246`). Sin embargo, `ResultScreen` nunca lee `result.recommendations` y muestra siempre 3 tarjetas estáticas idénticas.
2. **Valor clínico simulado engañoso cuando no hay historial (`HomeScreen.kt:421-427`):**
   Cuando `lastResult == null` (usuario nuevo o historial vacío), `HomeScreen` asigna `val hbValue = res?.hemoglobinLevel ?: 12.8` y `val severity = res?.severity ?: AnemiaSeverity.NORMAL`, mostrando en grande `12.8 g/dL` y el badge verde `"Nivel Normal (Sin anemia)"` junto con `"Sin chequeo previo aún"`, lo que puede confundirse con un resultado real.

## Plan de remediación (micro-pasos)
- [x] **Paso 1:** Renderizar las recomendaciones dinámicas de `result.recommendations` en `ResultScreen.kt` manteniendo el diseño visual clínico.
- [x] **Paso 2:** Mostrar un estado vacío claro (`"--.- g/dL"` / `"Sin Evaluar"`) en `HomeScreen.kt` cuando `lastResult == null`, en lugar de mostrar `12.8 g/dL` y `"Nivel Normal"`.

## Bitácora
- **2026-10-16 Detectado en auditoría exhaustiva:** Registrado como `Pendiente`.
- **2026-10-16 Remediado:** Renderizado dinámico de `result.recommendations` en `ResultScreen.kt` y estado vacío explícito (`"--.-"` g/dL y badge `"Sin Evaluar"`) en `HomeScreen.kt`. Estado actualizado a `Resuelto`.
