# QLT-0003: Complejidad cognitiva y composable monolítico en ScanScreen

- **Estado:** Resuelto
- **Severidad:** [Medium]
- **Regla Sonar:** `kotlin:S3776` (Cognitive Complexity) / `kotlin:S104` (File length) · Cualidad: Maintainability
- **Ubicación:** `app/src/main/java/com/anemia/detection/ui/screens/ScanScreen.kt:110`
- **Fecha detección:** 2026-10-10

## Descripción
`ScanScreen.kt` acumulaba más de 1,140 líneas y concentraba la orquestación de la cámara nativa, la selección de modo de captura (rápido vs guiado), la lógica de decodificación de fotos, el procesamiento asíncrono y los diálogos de advertencia clínica en un solo composable extenso.
Esto dificultaba el mantenimiento a largo plazo y las pruebas de interfaz aisladas.

## Plan de remediación (micro-pasos)
- [x] **Paso 1:** Extraer las tarjetas de modo de captura (`ModeOptionItem`), selección anatómica (`SampleOptionItem`), pasos de protocolo (`ProtocolStep`), consejos (`SingleShotCard`) y vista de procesamiento (`AnalyzingView`) a un archivo de componentes especializado `ScanOptionsComponents.kt` en `ui/components/`.

## Bitácora
- **2026-10-10 Paso 1 aplicado:** Se modularizaron ~300 líneas extrayendo los componentes de opciones y guía a `ScanOptionsComponents.kt`. Se redujo drásticamente el tamaño y la complejidad de `ScanScreen.kt`. Compilación verificada con éxito (`BUILD SUCCESSFUL in 22s`). Hallazgo resuelto.
