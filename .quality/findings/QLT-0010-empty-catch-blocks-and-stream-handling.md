# QLT-0010: Bloques catch vacíos y cierre manual de InputStream

- **Estado:** Resuelto
- **Severidad:** 🟡 Medium
- **Regla Sonar:** `kotlin:S108` / `kotlin:S2095` / `python:S5754` · Cualidad: Maintainability / Reliability
- **Ubicación:** `app/src/main/java/com/anemia/detection/ui/screens/ScanScreen.kt:260-262`, `app/src/main/java/com/anemia/detection/ui/screens/ScanScreen.kt:571`, `app/src/main/java/com/anemia/detection/engine/ImagePreprocessor.kt:35-45`, `training/evaluate_tflite_metrics.py:86-87,122-123,129-130`
- **Fecha detección:** 2026-10-16

## Descripción
1. En `ScanScreen.kt:260-262` y `ScanScreen.kt:571`, los bloques `catch (e: Exception)` están vacíos sin registrar advertencias (`Log.w`).
2. En `ImagePreprocessor.kt:35-45`, `loadBitmapFromUri` utiliza `var input: InputStream? = null` con `try / finally { input?.close() }` en lugar del bloque idiomático `.use { }` de Kotlin.
3. En `training/evaluate_tflite_metrics.py:86-87,122-123,129-130`, se usan `except Exception as e: pass` y `except:` desnudos (`bare except`).

## Plan de remediación (micro-pasos)
- [x] **Paso 1:** Refactorizar `ImagePreprocessor.loadBitmapFromUri` para usar `context.contentResolver.openInputStream(uri)?.use { ... }` y registrar errores con `Log.w`.
- [x] **Paso 2:** Añadir `Log.w` en los bloques `catch` de `ScanScreen.kt` y reemplazar los `except:` desnudos en `training/evaluate_tflite_metrics.py` por `except ValueError:` / `except (OSError, ValueError):` explícitos.

## Bitácora
- **2026-10-16 Detectado en auditoría exhaustiva:** Registrado como `Pendiente`.
- **2026-10-16 Remediado:** Aplicado `.use { }` y `Log.w` en `ImagePreprocessor.kt`, trazas `Log.w` en `ScanScreen.kt` y manejo explícito de excepciones en `evaluate_tflite_metrics.py`. Estado actualizado a `Resuelto`.
