# QLT-0012: Código muerto, dependencias Gradle sin uso y pérdida de estado de navegación al rotar

- **Estado:** Resuelto
- **Severidad:** 🟡 Medium
- **Regla Sonar:** `kotlin:S1144` (Unused code / dependencies) · Cualidad: Maintainability
- **Ubicación:** `app/src/main/java/com/anemia/detection/ui/components/HardwareInfoCard.kt:45`, `app/build.gradle.kts:4,81-83`, `app/src/main/java/com/anemia/detection/MainNavigation.kt:30`, `scripts/train_and_export_models.py:1`
- **Fecha detección:** 2026-10-16

## Descripción
1. **Componente huérfano (`HardwareInfoCard.kt`):** Contiene 215 líneas de código que no se invocan desde ninguna pantalla (el diagnóstico de hardware fue integrado directamente en `SettingsScreen.kt`).
2. **Dependencias y plugins sin uso en `app/build.gradle.kts`:** Se declaran `androidx.navigation3.ui`, `androidx.navigation3.runtime`, `androidx.lifecycle.viewmodel.navigation3` y el plugin `kotlin.serialization` (además de `tensorflow-lite-support` en `libs.versions.toml`), ninguno de los cuales se utiliza en el código fuente.
3. **Estado de navegación volátil en `MainNavigation.kt:30`:** `var currentDestination by remember { mutableStateOf<AppDestination>(AppDestination.Home) }` pierde la pantalla actual ante cualquier cambio de configuración (rotación de pantalla, cambio de idioma o modo oscuro).
4. **Script ML obsoleto (`scripts/train_and_export_models.py`):** Duplica de forma incompatible la lógica ya reemplazada por `training/models.py` y `training/export_tflite.py`.

## Plan de remediación (micro-pasos)
- [x] **Paso 1:** Limpiar plugins y dependencias no usadas en `app/build.gradle.kts` y `gradle/libs.versions.toml`.
- [x] **Paso 2:** Reutilizar `HardwareInfoCard.kt` en `SettingsScreen.kt` y preservar el estado de navegación en `MainNavigation.kt` ante cambios de configuración mediante `rememberSaveable`.

## Bitácora
- **2026-10-16 Detectado en auditoría exhaustiva:** Registrado como `Pendiente`.
- **2026-10-16 Remediado:** Eliminadas dependencias `navigation3`, `tensorflow-lite-support` y plugin `kotlin-serialization` sin uso; reutilizado `HardwareInfoCard` en `SettingsScreen.kt`; añadido `rememberSaveable` con `AppDestinationSaver` en `MainNavigation.kt`. Estado actualizado a `Resuelto`.
