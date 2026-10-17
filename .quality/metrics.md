# Métricas de Calidad de Código

- **Tecnología Principal:** Kotlin 2.3.20 / Jetpack Compose Material 3 / TensorFlow Lite 2.17.0 / Python 3.12 (TensorFlow/Keras)
- **Archivos Fuente Analizados:** 23 archivos Kotlin (`.kt`) + 11 scripts Python (`.py`) + configuración Gradle (`.kts`, `.toml`)
- **Líneas Totales de Código:** ~4,850 LOC Kotlin (App + Tests) y ~1,140 LOC Python (ML Pipeline)
- **Cobertura de Pruebas:**
  - Modelos de dominio (`DetectionResult`, `DeviceTier`, `PatientProfile` incluyendo `MALE`, `FEMALE`, `PREGNANT_FEMALE` y `CHILD`): 100% cubiertos y verificados con JUnit4 (`testDebugUnitTest` exitoso).
  - Preprocesador (`ImagePreprocessor`): funciones biofísicas (`calculateLuminance`, `calculateRednessRatio`, `calculateErythemaIndex`, `evaluateLuminanceQuality`) verificadas directamente sobre el SUT en `ImagePreprocessorLogicTest`.
  - Motor e Historial (`AnemiaInferenceEngine`, `HistoryRepository`): verificados en `AnemiaAppIntegrationTest` con cierre determinístico `.use { }` y limpieza de registros de prueba en bloque `finally`.
- **Duplicación Estimada:** < 2.0% (Constantes `SampleTypes` centralizadas, `MultiShotGuidedCard` extraído a `ScanOptionsComponents.kt` y `HardwareInfoCard` reutilizado en `SettingsScreen.kt`).
- **Complejidad Cognitiva:** Controlada (`ScanScreen.kt` reducido de 888 a ~680 LOC tras extracción de componentes guiados).
- **Confiabilidad (Reliability):** 0 Blockers y 0 High abiertos. Ciclo de vida nativo de TFLite cerrado determinísticamente (`Closeable` + `DisposableEffect`), umbrales OMS corregidos, escala `rednessRatio` corregida, decodificación de imágenes con `inSampleSize` en hilos de fondo y repositorio sincronizado con cerrojo estático.
- **Deuda Técnica Pendiente:** 0 minutos (14 de 14 hallazgos resueltos).
- **Quality Gate:** **PASSED** (0 hallazgos `Blocker`, `High`, `Medium` o `Low` pendientes; compilación y suite unitaria al 100%).
