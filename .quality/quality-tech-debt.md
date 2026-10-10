# Calidad — Tablero de Hallazgos y Deuda Técnica

| ID | Severidad | Regla Sonar | Cualidad | Hallazgo | Ubicación | Estado |
|---|---|---|---|---|---|---|
| QLT-0001 | 🔴 Blocker | `kotlin:S2095` | Reliability | Fuga de recursos en `loadModelFile`: `AssetFileDescriptor` y `FileInputStream` sin cerrar con `.use { }` | `AnemiaInferenceEngine.kt:39-48` | Resuelto |
| QLT-0002 | 🟠 High | `kotlin:S2885` | Reliability | Posible condición de carrera y duplicación de serialización SharedPreferences en `HistoryRepository` | `HistoryRepository.kt:28-66` | Resuelto |
| QLT-0003 | 🟡 Medium | `kotlin:S3776` | Maintainability | Complejidad cognitiva y archivo monolítico (>1100 líneas) en `ScanScreen` | `ScanScreen.kt:110` | Resuelto |
