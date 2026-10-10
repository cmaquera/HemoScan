# Calidad de código — HemoScan AI (Android / Kotlin)

Estado canónico de la calidad de software y buenas prácticas de desarrollo. Léelo antes de auditar o remediar.

## Estado de sincronización
- Lenguajes detectados: Kotlin (Jetpack Compose, CameraX, TensorFlow Lite), Groovy DSL (Gradle)
- Última actualización: 2026-10-10
- Ámbito: Calidad de código, confiabilidad y mantenibilidad bajo directrices SonarQube

## Contexto para IA (resumen denso)
- Stack y build: Kotlin 2.0.21, Android SDK 34/35, Jetpack Compose Material 3, TFLite 2.16.1
- Quality gate: **PASSED** (0 hallazgos abiertos, 3 hallazgos resueltos satisfactoriamente)
- Métricas actuales:
  - Cobertura estimada: Pruebas unitarias básicas en repositorio; pendiente cobertura en motor de inferencia.
  - Complejidad: Saneada tras extracción a `ScanOptionsComponents.kt`.
  - Recursos: Descriptores TFLite protegidos con `.use { }`.
- Hallazgos abiertos: 0 hallazgos pendientes (3 resueltos: QLT-0001 [Blocker], QLT-0002 [High], QLT-0003 [Medium]) — Ver [quality-tech-debt.md](quality-tech-debt.md)
- Reglas Sonar de referencia: `kotlin:S2095`, `kotlin:S3776`, `kotlin:S2885`

## Índice
- Tablero de hallazgos: `quality-tech-debt.md`
- Detalle por hallazgo: `findings/`
- Métricas: `metrics.md`
- Reglas cacheadas: `standards/`
