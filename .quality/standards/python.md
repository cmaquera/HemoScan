# Estándares SonarQube para Python (ML Pipeline)

Reglas canónicas de referencia pública utilizadas en las auditorías del pipeline de Machine Learning (`training/` y `scripts/`):

### 1. `python:S5754` — Bare "except:" should not be used / Exceptions should not be silently ignored
- **Cualidad:** Reliability / Maintainability
- **Severidad por defecto:** Medium / High
- **Descripción:** Usar `except:` sin especificar el tipo de excepción o silenciar excepciones con `pass` oculta errores de lectura de archivos, tensores corruptos o interrupciones del sistema.

### 2. `python:S1481` — Unused local variables and functions should be removed or wired
- **Cualidad:** Maintainability / Reliability
- **Severidad por defecto:** Medium / High
- **Descripción:** Definir generadores o variables de configuración (como `representative_data_gen()` en exportación TFLite) sin conectarlos al flujo de conversión produce artefactos con comportamiento distinto al esperado.
