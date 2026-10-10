# Guía de Contribución — HemoScan AI

Gracias por tu interés en colaborar con **HemoScan AI**. Este proyecto es una iniciativa de código abierto orientada a la salud digital móvil (mHealth) para la detección preliminar no invasiva de anemia y estimación de hemoglobina mediante visión por computador e inteligencia artificial en el dispositivo (*Edge AI*).

Toda contribución —desde mejoras en la arquitectura, optimizaciones de modelos neuronales, correcciones de errores, hasta refinamientos de accesibilidad y documentación— es bienvenida.

---

## 1. Principios y Filosofía del Proyecto

1. **Privacidad y Procesamiento en el Dispositivo (Offline-First):**
   Ninguna imagen o dato biométrico debe transmitirse obligatoriamente a servidores remotos. La inferencia debe funcionar de manera 100% autónoma en el dispositivo móvil sin depender de conectividad a internet.

2. **Rigor Clínico y Ética en Salud:**
   HemoScan AI es una herramienta de cribado preliminar y orientación médica (*Software as a Medical Device - SaMD*), no un dispositivo de diagnóstico definitivo de laboratorio. No debe presentarse a los usuarios como un sustituto de un hemograma completo (CBC) ni de la consulta médica profesional.

3. **Arquitectura Limpia y Calidad de Código (Clean Code):**
   El repositorio mantiene un estándar riguroso bajo la taxonomía de SonarQube (registrado en la carpeta `.quality/`). Todo código nuevo debe ser determinista, seguro ante concurrencia (*thread-safe*), libre de fugas de descriptores nativos y modular.

---

## 2. Requisitos del Entorno de Desarrollo

### Desarrollo Android (Aplicación Móvil)
* **IDE:** Android Studio (Ladybug 2024.2, Koala o superior).
* **JDK:** Java Development Kit (JDK) 17 o 21.
* **Android SDK:** 
  * Compile SDK: 35
  * Target SDK: 35
  * Min SDK: 26 (Android 8.0 Oreo o superior)
* **Build System:** Gradle 8.x / 9.x con Kotlin 2.0.21.
* **Dispositivo de Prueba:** Emulador con Android 8.0+ o dispositivo físico con depuración USB habilitada.

### Desarrollo de Modelos y Machine Learning (Opcional - Directorio `training/`)
* **Python:** Versión 3.10 a 3.12.
* **Bibliotecas:** TensorFlow 2.16+, NumPy, SciPy, Scikit-learn, Pillow, Pandas.

---

## 3. Configuración Inicial del Proyecto

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/cmaquera/HemoScan.git
   cd HemoScan
   ```

2. **Configurar el SDK de Android:**
   Crea o verifica el archivo `local.properties` en la raíz del proyecto indicando la ruta de tu SDK:
   ```properties
   sdk.dir=C\:\\Users\\TuUsuario\\AppData\\Local\\Android\\Sdk
   ```

3. **Compilar y validar:**
   ```powershell
   # En Windows PowerShell
   .\gradlew.bat assembleDebug
   .\gradlew.bat testDebugUnitTest
   ```
   ```bash
   # En Linux o macOS
   ./gradlew assembleDebug
   ./gradlew testDebugUnitTest
   ```

---

## 4. Estructura de la Arquitectura

El código fuente de la aplicación Android está organizado en capas dentro de `app/src/main/java/com/anemia/detection/`:

* **`models/` (Dominio y Datos):**
  * `DeviceTier.kt`: Estratificación de hardware (RAM, arquitectura) y selección adaptativa de modelo.
  * `PatientProfile.kt`: Categorías demográficas y umbrales de hemoglobina estandarizados por la Organización Mundial de la Salud (OMS).
  * `DetectionResult.kt`: Entidad inmutable con los resultados del análisis (hemoglobina, severidad, confianza, estado de sincronización).

* **`engine/` (Motores de Procesamiento e Inferencia):**
  * `DeviceProfiler.kt`: Diagnóstico de memoria RAM y capacidades del procesador.
  * `ImagePreprocessor.kt`: Normalización de imágenes a 224x224 píxeles, cálculo de luminancia ($Y$) y verificación de calidad de iluminación.
  * `AnemiaInferenceEngine.kt`: Orquestación de TensorFlow Lite con gestión segura de memoria nativa y conmutación automática de modelos (HemaViT, MobileNetV3 o modo analítico).

* **`storage/` (Persistencia):**
  * `HistoryRepository.kt`: Repositorio local sincronizado de forma atómica (`@Synchronized`) para prevenir condiciones de carrera.
  * `CloudSyncManager.kt`: Sincronización oportunista que evalúa conectividad real antes de realizar respaldos.

* **`ui/` (Interfaz Jetpack Compose):**
  * `components/`: Componentes modulares reutilizables (tacómetro `HemoglobinGauge`, guía nutricional `NutritionGuideDialog`, guía de captura `VisualCaptureGuideDialog`, tarjetas de opciones `ScanOptionsComponents`).
  * `screens/`: Pantallas principales (`HomeScreen`, `ScanScreen`, `ResultScreen`, `HistoryScreen`, `SettingsScreen`).
  * `theme/`: Sistema de diseño clínico (Clinical Precision AI) alineado con Google Material 3.

* **`training/` (Pipeline de Machine Learning):**
  Scripts de Python para descarga de datasets, entrenamiento multitaréa (clasificación binaria y regresión continua) y exportación a binarios TFLite cuantizados en INT8 (`app/src/main/assets/models/`).

---

## 5. Estándares de Calidad y Buenas Prácticas

Toda contribución debe respetar las directrices de Clean Code auditadas en `.quality/`:

1. **Gestión Determinista de Recursos (`kotlin:S2095`):**
   Cualquier descriptor de archivo (`AssetFileDescriptor`), canal (`FileChannel`), flujo (`FileInputStream`) o intérprete nativo debe cerrarse inmediatamente después de su uso mediante bloques idiomáticos `.use { }` o llamadas explícitas a `.close()`.

2. **Seguridad en Concurrencia (`kotlin:S2885`):**
   Las operaciones que leen y escriben sobre almacenamiento local compartido deben estar protegidas mediante sincronización atómica (`@Synchronized`) para evitar corrupción de datos entre corrutinas.

3. **Modularidad y Complejidad Cognitiva (`kotlin:S3776` / `kotlin:S104`):**
   Las funciones composables extensas deben dividirse en sub-componentes especializados con responsabilidades únicas y desacopladas.

4. **Tratamiento de Cadenas y Textos:**
   No utilices emojis en cadenas de texto, mensajes de confirmación ni documentación técnica. Emplea redacción formal en español neutro o inglés técnico.

---

## 6. Flujo de Trabajo para Contribuir

1. **Crear una Bifurcación (Fork):**
   Bifurca el repositorio en tu cuenta de GitHub y clónalo localmente.

2. **Crear una Rama de Trabajo:**
   Utiliza nombres descriptivos siguiendo el estándar:
   ```bash
   git checkout -b feature/optimizacion-preprocesamiento
   # o
   git checkout -b fix/fuga-memoria-camara
   # o
   git checkout -b docs/mejora-guia-instalacion
   ```

3. **Realizar Cambios y Pruebas:**
   Asegúrate de que todas las pruebas unitarias existentes se ejecuten satisfactoriamente:
   ```powershell
   .\gradlew.bat testDebugUnitTest
   ```
   Si agregas una nueva funcionalidad o motor de cálculo, incluye las correspondientes pruebas unitarias en `app/src/test/`.

4. **Formato de Commits:**
   Recomendamos la convención de *Conventional Commits*:
   * `feat: agregar soporte para calibración en luz cálida`
   * `fix: resolver retraso en liberación del stream de cámara`
   * `docs: actualizar referencias de datasets clínicos`
   * `refactor: modularizar diálogo de configuración médica`
   * `test: incorporar pruebas de regresión para cálculo de Youden`

5. **Enviar un Pull Request (PR):**
   * Abre el Pull Request contra la rama `main` del repositorio oficial (`cmaquera/HemoScan`).
   * Describe detalladamente la motivación del cambio, las pruebas realizadas y capturas de pantalla si involucra modificaciones en la interfaz de usuario.
   * Confirma que el Quality Gate no se vea afectado negativamente.

---

## 7. Áreas Prioritarias de Colaboración

Si buscas ideas para contribuir, las siguientes áreas son de alta prioridad:

* **Soporte de Delegados de Hardware (NNAPI / GPU):** Habilitación experimental de aceleración por GPU en TensorFlow Lite con degradación elegante (*fallback*) a CPU en caso de controladores incompatibles.
* **Internacionalización (i18n):** Extracción de textos a `strings.xml` para soporte multi-idioma (inglés, portugués, quechua).
* **Accesibilidad (a11y):** Optimización para lectores de pantalla (TalkBack) y tamaños de fuente dinámicos.
* **Validación Clínica y Datasets:** Integración de nuevas cohortes abiertas y evaluación con índices espectrales adicionales.

---

## 8. Licencia

Al contribuir a **HemoScan AI**, aceptas que tus aportes estarán licenciados bajo los términos de la **Licencia MIT** del proyecto, sujeta a los avisos de exención de responsabilidad médica (*Medical Disclaimer*).
