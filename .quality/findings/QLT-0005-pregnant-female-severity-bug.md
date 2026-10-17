# QLT-0005: Severidad Anemia Leve (MILD) inalcanzable y umbrales OMS incorrectos en mujeres gestantes

- **Estado:** Resuelto
- **Severidad:** 🔴 Blocker
- **Regla Sonar:** `kotlin:S1871` (Condiciones lógicas y ramas inalcanzables) · Cualidad: Reliability
- **Ubicación:** `app/src/main/java/com/anemia/detection/models/PatientProfile.kt:35-42`
- **Fecha detección:** 2026-10-16

## Descripción
En `PatientProfile.evaluateSeverity(hbLevel: Double)` se evaluaba `hbLevel >= 11.0 && whoThreshold > 11.0 -> AnemiaSeverity.MILD`. Como para `PatientCategory.PREGNANT_FEMALE` el `whoThreshold` es `11.0`, la condición `whoThreshold > 11.0` hacía que `AnemiaSeverity.MILD` fuera inalcanzable en gestantes, saltando directamente a `MODERATE`.

## Plan de remediación (micro-pasos)
- [x] **Paso 1:** Corregir `PatientProfile.evaluateSeverity(hbLevel: Double)` para contemplar los umbrales OMS específicos de `PREGNANT_FEMALE` (`10.0..10.9` Leve, `7.0..9.9` Moderada, `< 7.0` Severa) y del resto de cohortes.
- [x] **Paso 2:** Ampliar `PatientProfileTest.kt` con pruebas unitarias exhaustivas para `PREGNANT_FEMALE` y `CHILD` en todos los grados de severidad.

## Bitácora
- **2026-10-16 Detectado en auditoría exhaustiva:** Registrado como `Pendiente`.
- **2026-10-16 Paso 1 aplicado:** Se separaron en `PatientProfile.evaluateSeverity` los cortes OMS específicos de embarazo (`>= 10.0` Leve, `>= 7.0` Moderada, `< 7.0` Severa) respecto al resto de perfiles (`>= 11.0` Leve, `>= 8.0` Moderada, `< 8.0` Severa).
- **2026-10-16 Paso 2 aplicado:** Se añadieron `testSeverityClassificationForPregnantFemale` y `testSeverityClassificationForChild` en `PatientProfileTest.kt`. Hallazgo resuelto.
