# QLT-0015: Crash NullPointerException en Historial al deserializar registros antiguos con Gson

- **Estado:** Resuelto
- **Severidad:** 🔴 Blocker
- **Regla Sonar:** `kotlin:S2259` (Null pointers should not be dereferenced) · Cualidad: Reliability
- **Ubicación:** `app/src/main/java/com/anemia/detection/models/DetectionResult.kt:24-30`, `app/src/main/java/com/anemia/detection/storage/HistoryRepository.kt:18-27`, `app/src/main/java/com/anemia/detection/ui/screens/HistoryScreen.kt:308`
- **Fecha detección:** 2026-10-16

## Descripción
Al abrir la pantalla de Historial (`HistoryScreen`) en un dispositivo físico con registros guardados por versiones previas de la aplicación, la app sufría un cierre forzoso (`FATAL EXCEPTION: main` — `java.lang.NullPointerException: Parameter specified as non-null is null: method kotlin.text.StringsKt__StringsKt.contains, parameter <this> at DetectionResult.isNeuralInference(DetectionResult.kt:26)`).

**Causa raíz:** Gson utiliza reflexión (`Unsafe.allocateInstance`) sin invocar el constructor de Kotlin ni aplicar valores por defecto de parámetros ausentes en el JSON (`inferenceMethod`, `recommendations`, etc.), dejando propiedades declaradas como no-nulas en `null` en tiempo de ejecución (y `shotCount` en `0`).

## Plan de remediación (micro-pasos)
- [x] **Paso 1:** Hacer `DetectionResult.isNeuralInference`, `isMultiShot` y un nuevo método `DetectionResult.normalized()` defensivos frente a referencias `null` inyectadas por Gson y normalizar registros heredados (`shotCount = 0`, `rednessRatio > 5.0` e inconsistencias `isAnemic` vs `severity`).
- [x] **Paso 2:** Aplicar `.normalized()` y desduplicación de `id` en `HistoryRepository.getAllResults()` y añadir prueba unitaria con JSON heredado real en `DetectionResultTest.kt`.

## Bitácora
- **2026-10-16 Detectado en dispositivo físico (Motorola Edge 60 Fusion via ADB logcat):** Identificada traza exacta en `DetectionResult.isNeuralInference`.
- **2026-10-16 Remediado:** Implementado `DetectionResult.normalized()` y normalización automática en `HistoryRepository.getAllResults()`. Verificado con test unitario y en el dispositivo físico.
