# QLT-0007: Decodificación de Bitmaps e I/O en el hilo principal sin submuestreo

- **Estado:** Resuelto
- **Severidad:** 🟠 High
- **Regla Sonar:** `kotlin:S6305` (Main thread I/O & heavy CPU work) · Cualidad: Reliability
- **Ubicación:** `app/src/main/java/com/anemia/detection/ui/screens/ScanScreen.kt:201`, `app/src/main/java/com/anemia/detection/ui/screens/ScanScreen.kt:231`, `app/src/main/java/com/anemia/detection/ui/screens/ScanScreen.kt:248-263`, `app/src/main/java/com/anemia/detection/engine/ImagePreprocessor.kt:35-45`
- **Fecha detección:** 2026-10-16

## Descripción
En `ScanScreen` se realizan operaciones de decodificación de imágenes y recorrido de píxeles directamente en el hilo principal (UI Thread):
1. `preprocessor.loadBitmapFromUri(context, uri)` (`ScanScreen.kt:231` e `ImagePreprocessor.kt:35-45`) decodifica la imagen completa de la galería sin configurar `BitmapFactory.Options.inSampleSize`. En teléfonos con cámaras de 12–48 MP y `< 3.5 GB` de RAM, esto bloquea la UI o causa `OutOfMemoryError`.
2. `preprocessor.assessQuality(bitmap)` (`ScanScreen.kt:201`) y `loadDemoSamples()` (`ScanScreen.kt:248-263`) se ejecutan síncronamente en el hilo de eventos de Compose.

## Plan de remediación (micro-pasos)
- [x] **Paso 1:** Añadir submuestreo (`inSampleSize`) y uso de `.use { }` en `ImagePreprocessor.loadBitmapFromUri` para limitar la resolución en memoria antes de decodificar.
- [x] **Paso 2:** Mover la decodificación de galería, carga de muestras demo y evaluación de calidad (`assessQuality`) a `Dispatchers.IO` / `Dispatchers.Default` dentro de `coroutineScope.launch`.

## Bitácora
- **2026-10-16 Detectado en auditoría exhaustiva:** Registrado como `Pendiente`.
- **2026-10-16 Remediado:** Añadido submuestreo `calculateInSampleSize` (`MAX_DECODE_DIMENSION = 1024`) en `ImagePreprocessor.kt` y movidas las operaciones de decodificación y evaluación de calidad a `Dispatchers.IO` / `Dispatchers.Default` en `ScanScreen.kt`. Estado actualizado a `Resuelto`.
