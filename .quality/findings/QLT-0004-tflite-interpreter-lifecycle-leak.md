# QLT-0004: Fuga de memoria nativa del intérprete TFLite al desmontar ScanScreen

- **Estado:** Resuelto
- **Severidad:** 🔴 Blocker
- **Regla Sonar:** `kotlin:S2095` ("Resources should be closed") · Cualidad: Reliability
- **Ubicación:** `app/src/main/java/com/anemia/detection/ui/screens/ScanScreen.kt:123`, `app/src/main/java/com/anemia/detection/engine/AnemiaInferenceEngine.kt:26-35`, `app/src/androidTest/java/com/anemia/detection/AnemiaAppIntegrationTest.kt:63`
- **Fecha detección:** 2026-10-16

## Descripción
`AnemiaInferenceEngine` mantiene una referencia a `org.tensorflow.lite.Interpreter` (que reserva tensores y memoria nativa C++ fuera del heap de la JVM) y expone el método `close()`.
Sin embargo:
1. `AnemiaInferenceEngine` no implementa `java.io.Closeable` / `AutoCloseable`.
2. En `ScanScreen.kt:123`, se instancia `val inferenceEngine = remember { AnemiaInferenceEngine(context) }`, pero nunca se registra un `DisposableEffect(inferenceEngine) { onDispose { inferenceEngine.close() } }`.
3. En `AnemiaAppIntegrationTest.kt:63`, el motor tampoco se cierra al finalizar la prueba.

Cada vez que el usuario entra a `ScanScreen`, ejecuta un chequeo y vuelve al inicio o a resultados, el `Interpreter` nativo queda huérfano sin liberarse determinísticamente, acumulando memoria nativa en dispositivos de gama baja.

## Plan de remediación (micro-pasos)
- [x] **Paso 1:** Hacer que `AnemiaInferenceEngine` implemente `java.io.Closeable` marcando `override fun close()`.
- [x] **Paso 2:** Añadir `DisposableEffect(inferenceEngine) { onDispose { inferenceEngine.close() } }` en `ScanScreen.kt` (y `.use { }` en `AnemiaAppIntegrationTest.kt`).

## Bitácora
- **2026-10-16 Detectado en auditoría exhaustiva:** Registrado como `Pendiente`.
- **2026-10-16 Paso 1 aplicado:** `AnemiaInferenceEngine` ahora implementa `java.io.Closeable` y declara `override fun close()`, habilitando el uso de `.use { }` y el contrato estándar de liberación de recursos en Kotlin.
- **2026-10-16 Paso 2 aplicado:** Se registró `DisposableEffect(inferenceEngine)` en `ScanScreen.kt` para cerrar el intérprete nativo de TFLite al salir de la pantalla, y se envolvió con `.use { }` la instancia en `AnemiaAppIntegrationTest.kt`. Hallazgo resuelto.
