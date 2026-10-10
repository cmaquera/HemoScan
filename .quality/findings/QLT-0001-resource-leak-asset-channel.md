# QLT-0001: Fuga de descriptores de archivos en carga de modelo TFLite

- **Estado:** Resuelto
- **Severidad:** 🔴 Blocker
- **Regla Sonar:** `kotlin:S2095` ("Resources should be closed") · Cualidad: Reliability
- **Ubicación:** `app/src/main/java/com/anemia/detection/engine/AnemiaInferenceEngine.kt:39-48`
- **Fecha detección:** 2026-10-10

## Descripción
En `AnemiaInferenceEngine.loadModelFile`, se abren instancias de `context.assets.openFd(assetPath)`, `FileInputStream` y `FileChannel` para mapear el búfer de memoria del modelo TFLite. 
Ninguno de estos objetos se cerraba explícitamente ni se envolvía con `.use { }`.
En Android, dejar abiertos `AssetFileDescriptor` y `FileInputStream` agota los descriptores de archivos del proceso (FD exhaustion), provocando eventuales excepciones `IOException: Too many open files` o bloqueos de memoria nativa tras múltiples chequeos.

## Plan de remediación (micro-pasos)
- [x] **Paso 1:** Envolver `openFd`, `FileInputStream` y `channel` dentro de bloques `.use { }` idiomáticos de Kotlin para garantizar el cierre determinista del descriptor, manteniendo intacto el `ByteBuffer` mapeado en memoria.

## Bitácora
- **2026-10-10 Paso 1 aplicado:** Se reemplazó la instanciación abierta por `.use { fileDescriptor -> FileInputStream(...).use { inputStream -> inputStream.channel.use { fileChannel -> ... } } }`. Verificado con compilación exitosa (`BUILD SUCCESSFUL`). Hallazgo resuelto.
