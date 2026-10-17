# Calidad de código — HemoScan AI (Android / Kotlin + ML Pipeline Python)

Estado canónico de la calidad de software y buenas prácticas de desarrollo. Léelo antes de auditar o remediar.

## Estado de sincronización
- Lenguajes detectados: Kotlin 2.3.20 (Jetpack Compose, TensorFlow Lite), Kotlin DSL (Gradle 9.0.1), Python 3.12 (TensorFlow / Scikit-Learn)
- Último commit documentado: `f8450e5`
- Última actualización: 2026-10-16
- Ámbito auditado: Proyecto completo (`app/src/main`, `app/src/test`, `app/src/androidTest`, `training/`, `scripts/`, `gradle/`)

## Contexto para IA (resumen denso)
- Stack y build: Kotlin 2.3.20, AGP 9.0.1, Android SDK 36 (minSdk 24), Jetpack Compose BOM 2026.03.01, TFLite 2.17.0, JUnit 4.13.2
- Quality gate: **PASSED** (0 hallazgos pendientes; 14 de 14 hallazgos remediados y verificados con `testDebugUnitTest` y `assembleDebug`)
- Métricas actuales:
  - Cobertura: 100% de perfiles OMS (`MALE`, `FEMALE`, `PREGNANT_FEMALE`, `CHILD`) y métodos biofísicos reales de `ImagePreprocessor` cubiertos en unit tests; `AnemiaAppIntegrationTest` con limpieza determinística.
  - Complejidad y Mantenibilidad: `MultiShotGuidedCard` y `SampleTypes` extraídos a `ScanOptionsComponents.kt`, `HardwareInfoCard` reutilizado en `SettingsScreen.kt`, navegación persistente con `rememberSaveable` y dependencias Gradle depuradas.
  - Confiabilidad: `AnemiaInferenceEngine` implementa `Closeable` y se libera en `DisposableEffect` (`QLT-0004`), umbrales OMS de embarazo corregidos (`QLT-0005`), escala `rednessRatio` y flag `isAnemic` alineados (`QLT-0006`), `HistoryRepository` sincronizado con cerrojo estático (`QLT-0002`), decodificación de imágenes con `inSampleSize` fuera del hilo principal (`QLT-0007`) y pipeline ML con partición agrupada por paciente sin fuga de datos (`QLT-0009`).
- Hallazgos abiertos: **0 pendientes** y **14 resueltos** (`QLT-0001` a `QLT-0014`) — Ver [quality-tech-debt.md](quality-tech-debt.md)
- Reglas Sonar de referencia: `kotlin:S2095`, `kotlin:S1871`, `kotlin:S2184`, `kotlin:S2886`, `kotlin:S6305`, `kotlin:S3776`, `kotlin:S108`, `kotlin:S1192`, `kotlin:S1144`, `kotlin:S2699`, `python:S1481`, `python:S5754` (ver `standards/kotlin.md` y `standards/python.md`)

## Índice
- Tablero de hallazgos: [quality-tech-debt.md](quality-tech-debt.md)
- Detalle por hallazgo: `findings/` (`QLT-0001` a `QLT-0014`)
- Métricas: [metrics.md](metrics.md)
- Reglas cacheadas: [standards/kotlin.md](standards/kotlin.md), [standards/python.md](standards/python.md)
