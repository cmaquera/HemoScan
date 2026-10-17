# QLT-0014: Desfase de versiones en documentación de calidad y color de contenedor para severidad moderada

- **Estado:** Resuelto
- **Severidad:** 🟢 Low
- **Regla Sonar:** Convenciones y consistencia visual/documental · Cualidad: Maintainability
- **Ubicación:** `.quality/README.md:11`, `app/src/main/java/com/anemia/detection/ui/components/HemoglobinGauge.kt:70`, `app/src/main/java/com/anemia/detection/theme/Color.kt:33`
- **Fecha detección:** 2026-10-16

## Descripción
1. En `HemoglobinGauge.kt:70`, el color de fondo del badge para `AnemiaSeverity.MODERATE` usa `SeveritySevereContainer` (rojo claro `0xFFFEF2F2`) en lugar de un contenedor naranja suave acorde a `SeverityModerate` (como el `Color(0xFFFFF7ED)` usado en `HomeScreen.kt:439`).
2. En `.quality/README.md`, las versiones del stack indicaban Kotlin `2.0.21`, SDK `34/35` y TFLite `2.16.1`, mientras que `gradle/libs.versions.toml` y `app/build.gradle.kts` utilizan Kotlin `2.3.20`, `compileSdk = 36 / targetSdk = 36` y TFLite `2.17.0`.

## Plan de remediación (micro-pasos)
- [x] **Paso 1:** Definir `SeverityModerateContainer = Color(0xFFFFF7ED)` en `Color.kt` y usarlo consistentemente en `HemoglobinGauge.kt` y `HomeScreen.kt`.

## Bitácora
- **2026-10-16 Detectado en auditoría exhaustiva:** Versiones de `.quality/README.md` actualizadas durante la sincronización; queda `Pendiente` el ajuste de `SeverityModerateContainer` en código.
- **2026-10-16 Remediado:** Añadido `SeverityModerateContainer` en `Color.kt` y unificado en `HemoglobinGauge.kt` y `HomeScreen.kt`. Estado actualizado a `Resuelto`.
