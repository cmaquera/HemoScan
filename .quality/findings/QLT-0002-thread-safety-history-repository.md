# QLT-0002: Sincronización entre múltiples instancias en persistencia SharedPreferences

- **Estado:** Resuelto
- **Severidad:** 🟠 High
- **Regla Sonar:** `kotlin:S2886` / `kotlin:S2885` · Cualidad: Reliability
- **Ubicación:** `app/src/main/java/com/anemia/detection/storage/HistoryRepository.kt:17-76`, `app/src/main/java/com/anemia/detection/storage/CloudSyncManager.kt:25`
- **Fecha detección:** 2026-10-10 (Revalidado: 2026-10-16)

## Descripción
En la primera iteración se centralizó `persistResults` y se anotaron los métodos de `HistoryRepository` con `@Synchronized` de instancia. Como varias pantallas y `CloudSyncManager` crean instancias independientes de `HistoryRepository(context)`, se requería un monitor estático compartido para evitar condiciones de carrera entre instancias.

## Plan de remediación (micro-pasos)
- [x] **Paso 1:** Centralizar la persistencia en una función privada `persistResults(list: List<DetectionResult>)`.
- [x] **Paso 2:** Añadir `@Synchronized` inicial en métodos de acceso.
- [x] **Paso 3:** Reemplazar la sincronización por instancia (`@Synchronized`) por un objeto monitor estático compartido en `companion object { private val LOCK = Any() }` usando `synchronized(LOCK) { ... }` y `context.applicationContext` en `HistoryRepository`.

## Bitácora
- **2026-10-10 Pasos 1 y 2 aplicados:** Se centralizó la serialización en `persistResults` y se decoraron las operaciones con `@Synchronized`.
- **2026-10-16 Paso 3 aplicado:** Se reemplazó el monitor de instancia por `synchronized(LOCK)` sobre un `LOCK` estático en `companion object` y se usó `context.applicationContext` para garantizar exclusión mutua real entre todas las pantallas y `CloudSyncManager`. Hallazgo resuelto.
