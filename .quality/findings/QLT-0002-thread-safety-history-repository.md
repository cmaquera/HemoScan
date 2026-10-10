# QLT-0002: Sincronización y duplicación en persistencia SharedPreferences

- **Estado:** Resuelto
- **Severidad:** 🟠 High
- **Regla Sonar:** `kotlin:S2885` / `kotlin:S1192` · Cualidad: Reliability / Maintainability
- **Ubicación:** `app/src/main/java/com/anemia/detection/storage/HistoryRepository.kt:28-66`
- **Fecha detección:** 2026-10-10

## Descripción
En `HistoryRepository`, los métodos `saveResult`, `deleteResult`, `markAsSynced` y `markAllAsSynced` leen la lista actual desde `SharedPreferences`, la modifican en memoria y la re-serializan a JSON repitiendo `prefs.edit().putString(KEY_HISTORY, json).apply()`.
Si dos corrutinas concurrentes (por ejemplo, la sincronización en segundo plano con la nube y el guardado de un nuevo escaneo en primer plano) acceden simultáneamente, se pueden perder registros por condiciones de carrera.

## Plan de remediación (micro-pasos)
- [x] **Paso 1:** Centralizar la persistencia en una función privada `persistResults(list: List<DetectionResult>)`.
- [x] **Paso 2:** Añadir sincronización de hilo (`@Synchronized`) en las operaciones de escritura y lectura para evitar carreras concurrentes entre el escaneo y el sincronizador de nube.

## Bitácora
- **2026-10-10 Pasos 1 y 2 aplicados:** Se centralizó la serialización en `persistResults` y se decoraron todas las operaciones de acceso con `@Synchronized`. Compilación verificada con éxito (`BUILD SUCCESSFUL in 15s`). Hallazgo resuelto.
