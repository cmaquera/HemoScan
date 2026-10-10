# Estándares SonarQube para Kotlin

Reglas canónicas de referencia pública utilizadas en las auditorías de calidad de HemoScan AI:

### 1. `kotlin:S2095` — Resources should be closed
- **Cualidad:** Reliability
- **Severidad por defecto:** Blocker
- **Descripción:** Las conexiones I/O, flujos de entrada/salida (`InputStream`, `OutputStream`), canales (`FileChannel`) y descriptores nativos (`AssetFileDescriptor`) deben cerrarse determinísticamente mediante bloques `use { ... }` o en una cláusula `finally`. Dejar descriptores abiertos agota los límites del sistema operativo móvil.

### 2. `kotlin:S2885` — Thread-safe access to shared resources
- **Cualidad:** Reliability
- **Severidad por defecto:** High
- **Descripción:** Cuando múltiples hilos o corrutinas leen y modifican recursos de almacenamiento o estado en memoria compartida (como repositorios o caché), las operaciones críticas deben estar sincronizadas (`@Synchronized` o primitivas concurrentes) para evitar carreras de datos e inconsistencias.

### 3. `kotlin:S3776` — Cognitive Complexity of functions should not be too high
- **Cualidad:** Maintainability
- **Severidad por defecto:** Medium / High
- **Descripción:** Las funciones complejas con múltiples niveles de anidamiento de estructuras de control (`if`, `when`, `for`, lambdas anidadas) aumentan drásticamente el esfuerzo de comprensión y la propensión a errores. Se recomienda descomponer en funciones pequeñas con responsabilidad única.

### 4. `kotlin:S104` — Files should not have too many lines of code
- **Cualidad:** Maintainability
- **Severidad por defecto:** Low / Medium
- **Descripción:** Los archivos con más de 1,000 líneas de código deben modularizarse dividiendo sus responsabilidades en submódulos o componentes independientes.
