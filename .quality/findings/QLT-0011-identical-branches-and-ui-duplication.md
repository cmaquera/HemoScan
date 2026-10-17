# QLT-0011: Ramas condicionales idénticas, literales duplicados y extensión de HomeScreen y ScanScreen

- **Estado:** Resuelto
- **Severidad:** 🟡 Medium
- **Regla Sonar:** `kotlin:S1871` / `kotlin:S1192` / `kotlin:S3776` · Cualidad: Maintainability
- **Ubicación:** `app/src/main/java/com/anemia/detection/ui/screens/ScanScreen.kt:718-722`, `app/src/main/java/com/anemia/detection/ui/screens/HomeScreen.kt:98-857`
- **Fecha detección:** 2026-10-16

## Descripción
1. **Ramas idénticas (`kotlin:S1871`) en `ScanScreen.kt:718-722`:**
   ```kotlin
   val stepLabel = if (sampleType.contains("Ojo")) {
       if (i < 2) "Der #${i + 1}" else "Izq #${i - 1}"
   } else {
       if (i < 2) "Der #${i + 1}" else "Izq #${i - 1}"
   }
   ```
   Ambas ramas devuelven exactamente el mismo texto.
2. **Literales duplicados (`kotlin:S1192`):** Los valores `"Interior del Ojo"` y `"Uña de la Mano"` se repiten como literales mágicos en `HomeScreen.kt`, `ScanScreen.kt` y `ScanOptionsComponents.kt`.
3. **Extensión de archivos UI (`kotlin:S3776` / `kotlin:S104`):** `ScanScreen.kt` (888 líneas, aún con `MultiShotGuidedCard` embebido) puede modularizarse aún más.

## Plan de remediación (micro-pasos)
- [x] **Paso 1:** Simplificar la condición redundante en `ScanScreen.kt:718-722` y extraer `MultiShotGuidedCard` a `ScanOptionsComponents.kt`.
- [x] **Paso 2:** Centralizar las constantes de tipo de muestra (`SampleTypes.EYE`, `SampleTypes.NAIL`) para eliminar literales duplicados.

## Bitácora
- **2026-10-16 Detectado en auditoría exhaustiva:** Registrado como `Pendiente`.
- **2026-10-16 Remediado:** Extraído `MultiShotGuidedCard` y objeto `SampleTypes` a `ScanOptionsComponents.kt` (reduciendo `ScanScreen.kt` en ~220 líneas y eliminando la rama `if/else` idéntica). Estado actualizado a `Resuelto`.
