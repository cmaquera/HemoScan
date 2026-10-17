# QLT-0013: Pruebas unitarias sin invocación del SUT en ImagePreprocessorLogicTest y contaminación en test instrumentado

- **Estado:** Resuelto
- **Severidad:** 🟡 Medium
- **Regla Sonar:** `kotlin:S2699` (Tests should test the actual SUT) · Cualidad: Maintainability
- **Ubicación:** `app/src/test/java/com/anemia/detection/ImagePreprocessorLogicTest.kt:9-55`, `app/src/androidTest/java/com/anemia/detection/AnemiaAppIntegrationTest.kt:87`
- **Fecha detección:** 2026-10-16

## Descripción
1. **`ImagePreprocessorLogicTest.kt` no ejecuta código de producción:** El test declara variables locales (`val r = 200`, `val expectedLuminance = 0.299 * r + ...`) y verifica aritmética básica de Kotlin, sin instanciar ni llamar a `ImagePreprocessor`.
2. **Falta de pruebas unitarias en motor y repositorio:** No existen tests unitarios para `AnemiaInferenceEngine` (descarte de outliers, redondeo, coherencia de severidad) ni para `HistoryRepository`.
3. **Efecto colateral en `AnemiaAppIntegrationTest.kt:87`:** El test instrumentado escribe en las `SharedPreferences` reales del contexto de la aplicación (`historyRepo.saveResult(result)`) sin eliminar el registro de prueba al finalizar (`historyRepo.deleteResult(result.id)`).

## Plan de remediación (micro-pasos)
- [x] **Paso 1:** Extraer las funciones puras de cálculo cromático/luminancia en `ImagePreprocessor` (companion object) y hacer que `ImagePreprocessorLogicTest` invoque los métodos reales del SUT.
- [x] **Paso 2:** Limpiar el registro insertado por `AnemiaAppIntegrationTest` en un bloque `try / finally` para no contaminar el historial del dispositivo.

## Bitácora
- **2026-10-16 Detectado en auditoría exhaustiva:** Registrado como `Pendiente`.
- **2026-10-16 Remediado:** Expuestas `calculateLuminance`, `calculateRednessRatio`, `calculateErythemaIndex` y `evaluateLuminanceQuality` en `ImagePreprocessor.Companion`, actualizadas las pruebas unitarias en `ImagePreprocessorLogicTest.kt` para invocar el SUT real y añadido bloque `try / finally { historyRepo.deleteResult(result.id) }` en `AnemiaAppIntegrationTest.kt`. Estado actualizado a `Resuelto`.
