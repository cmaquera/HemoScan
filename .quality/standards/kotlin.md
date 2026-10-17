# Estándares SonarQube para Kotlin

Reglas canónicas de referencia pública utilizadas en las auditorías de calidad de HemoScan AI:

### 1. `kotlin:S2095` — Resources should be closed
- **Cualidad:** Reliability
- **Severidad por defecto:** Blocker
- **Descripción:** Las conexiones I/O, flujos de entrada/salida (`InputStream`, `OutputStream`), canales (`FileChannel`), descriptores nativos (`AssetFileDescriptor`) e intérpretes nativos (`org.tensorflow.lite.Interpreter`) deben cerrarse determinísticamente mediante bloques `use { ... }`, `DisposableEffect` en Compose o una cláusula `finally`. Dejar recursos abiertos agota descriptores o memoria C++ fuera del heap de la JVM.

### 2. `kotlin:S2885` / `kotlin:S2886` — Thread-safe access and synchronization on shared resources
- **Cualidad:** Reliability
- **Severidad por defecto:** High
- **Descripción:** Cuando múltiples hilos o corrutinas leen y modifican recursos compartidos (como `SharedPreferences`), la sincronización debe realizarse sobre un monitor compartido real (p. ej. un objeto lock en `companion object` o un singleton). Usar `@Synchronized` en métodos de instancia cuando existen múltiples instancias de la clase no impide condiciones de carrera.

### 3. `kotlin:S1871` — Two branches in a conditional structure should not have exactly the same implementation
- **Cualidad:** Maintainability / Reliability
- **Severidad por defecto:** Medium / High
- **Descripción:** Tener dos ramas `if/else` o `when` con implementación idéntica o condiciones solapadas que hacen inalcanzable un caso lógico suele indicar un error de copia/pegado o un bug funcional en las reglas de negocio.

### 4. `kotlin:S2184` — Math operands should be cast before assignment / precision loss
- **Cualidad:** Reliability
- **Severidad por defecto:** High / Blocker
- **Descripción:** Las operaciones aritméticas de redondeo, truncamiento entero (`.toInt()`) y divisores de escala deben mantener consistencia dimensional para evitar pérdida de precisión o errores de factor de escala (p. ej. dividir por `10.0` en vez de `100.0`).

### 5. `kotlin:S6305` — Main thread should not be blocked by I/O or heavy CPU work
- **Cualidad:** Reliability
- **Severidad por defecto:** High
- **Descripción:** En Android/Compose, la decodificación de `Bitmap` desde URIs o assets y el recorrido intensivo de píxeles deben ejecutarse en `Dispatchers.IO` o `Dispatchers.Default`, aplicando submuestreo (`BitmapFactory.Options.inSampleSize`) para evitar tirones de UI (jank/ANR) y `OutOfMemoryError`.

### 6. `kotlin:S3776` — Cognitive Complexity of functions should not be too high
- **Cualidad:** Maintainability
- **Severidad por defecto:** Medium / High
- **Descripción:** Las funciones complejas con múltiples niveles de anidamiento de estructuras de control (`if`, `when`, `for`, lambdas anidadas) aumentan drásticamente el esfuerzo de comprensión y la propensión a errores. Se recomienda descomponer en funciones pequeñas con responsabilidad única.

### 7. `kotlin:S104` — Files should not have too many lines of code
- **Cualidad:** Maintainability
- **Severidad por defecto:** Low / Medium
- **Descripción:** Los archivos excesivamente extensos deben modularizarse dividiendo sus responsabilidades en submódulos o componentes independientes.

### 8. `kotlin:S108` — Nested blocks of code should not be left empty
- **Cualidad:** Maintainability / Reliability
- **Severidad por defecto:** Medium
- **Descripción:** Los bloques `catch` vacíos silencian fallos en tiempo de ejecución y dificultan el diagnóstico de errores. Deben registrar la excepción (`Log.w`/`Log.e`) o manejar el estado de fallo explícitamente.

### 9. `kotlin:S1192` — String literals should not be duplicated
- **Cualidad:** Maintainability
- **Severidad por defecto:** Medium
- **Descripción:** Los literales de texto repetidos en múltiples archivos (como identificadores de tipo de muestra o formatos de fecha) deben extraerse a constantes o `enum class` para evitar errores tipográficos al refactorizar.

### 10. `kotlin:S1144` — Unused "private" or unreferenced declarations should be removed
- **Cualidad:** Maintainability
- **Severidad por defecto:** Medium
- **Descripción:** El código muerto (componentes, funciones o dependencias no referenciadas en todo el proyecto) incrementa el costo de mantenimiento y el tamaño de compilación sin aportar valor funcional.

### 11. `kotlin:S2699` — Tests should include assertions on the system under test
- **Cualidad:** Maintainability
- **Severidad por defecto:** Medium
- **Descripción:** Las pruebas unitarias deben instanciar e invocar los métodos reales de la clase bajo prueba (SUT) en lugar de replicar fórmulas en variables locales del propio test.
