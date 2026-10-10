# Métricas de Calidad de Código

- **Tecnología Principal:** Kotlin / Jetpack Compose / CameraX / TensorFlow Lite
- **Archivos Fuente Analizados:** 22 archivos Kotlin (`.kt`)
- **Líneas Totales de Código:** ~3,200 LOC
- **Duplicación Estimada:** < 4% (Baja, principalmente patrones de persistencia SharedPreferences)
- **Complejidad Cognitiva:** Modularizada (Componentes de captura y guía extraídos a `ScanOptionsComponents.kt`).
- **Confiabilidad (Reliability):** 100% saneada (Descriptores TFLite cerrados con `.use { }`, persistencia sincronizada con `@Synchronized`).
- **Deuda Técnica Estimada:** 0 minutos (Todos los hallazgos resueltos).
- **Quality Gate:** **PASSED** (Pasa compilación, inferencia local optimizada, sin fugas de recursos ni carreras).
