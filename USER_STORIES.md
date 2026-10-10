# Historias de Usuario para Validación de HemoScan AI (APK Android)

Documento de especificación y validación de Historias de Usuario bajo metodología ágil (formato Rol-Acción-Beneficio) con Criterios de Aceptación (Gherkin / BDD) para pruebas en el emulador y dispositivos físicos.

---

### HU-01: Detección y Análisis 100% Offline (Sin Internet)
* **Como** profesional de salud o paciente en zonas rurales o centros sin cobertura,
* **Quiero** realizar capturas y obtener la estimación de hemoglobina sin conexión a internet ni llamadas a servidores externos,
* **Para** recibir resultados inmediatos y proteger la confidencialidad de mis imágenes médicas.

#### Criterios de Aceptación (BDD)
* **Escenario 1.1:** Ejecución en Modo Avión / Sin Red
  * **Dado que** el teléfono se encuentra en Modo Avión (WiFi y Datos Móviles desactivados).
  * **Cuando** el usuario pulsa "Iniciar Nuevo Análisis", captura una imagen y solicita el procesamiento.
  * **Entonces** el modelo de IA procesa la imagen localmente en el procesador del teléfono y muestra el resultado en menos de 1.5 segundos sin arrojar errores de red.
* **Escenario 1.2:** Insignia Visual de Estado Offline
  * **Dado que** el usuario está en la pantalla principal (Home).
  * **Entonces** se visualiza la insignia "Offline" confirmando que no hay telemetría ni transferencia a servidores.

---

### HU-02: Detección Adaptativa de Gama y Switch de Modelo IA
* **Como** usuario con un teléfono de gama baja (< 3.5 GB de RAM o Android Go),
* **Quiero** que la aplicación identifique automáticamente las capacidades de mi hardware y configure el modelo **MobileNetV3 (INT8)**,
* **Para** prevenir bloqueos, congelamientos de pantalla o sobrecalentamiento de la batería.

#### Criterios de Aceptación (BDD)
* **Escenario 2.1:** Detección en teléfono económico (< 3.5 GB RAM)
  * **Dado que** el dispositivo tiene 2.4 GB de RAM (como el emulador de pruebas).
  * **Cuando** se abre la aplicación,
  * **Entonces** la tarjeta de hardware muestra: "Gama Evaluada: Gama Baja / Económica" y activa el modelo "MobileNetV3 (Auto: Gama Baja)".
* **Escenario 2.2:** Forzar manualmente el modelo HemaViT
  * **Dado que** el usuario ingresa a "Configuración y Diagnóstico".
  * **Cuando** selecciona la opción "HemaViT (Máxima Precisión)",
  * **Entonces** la app guarda la preferencia y ejecuta el Vision Transformer en los análisis posteriores.

---

### HU-03: Control de Calidad de Iluminación y Flash
* **Como** evaluador,
* **Quiero** que la aplicación alerte si la toma fotográfica tiene iluminación deficiente o sombras severas,
* **Para** evitar mediciones cromáticas distorsionadas.

#### Criterios de Aceptación (BDD)
* **Escenario 3.1:** Imagen con iluminación adecuada
  * **Dado que** la foto tiene una luminancia promedio $Y \ge 40$ y $\le 245$ (con flash o luz homogénea).
  * **Cuando** se envía al analizador,
  * **Entonces** pasa directamente a las fases de normalización e inferencia.
* **Escenario 3.2:** Foto tomada en penumbra / sin flash
  * **Dado que** la luminancia es $Y < 40$.
  * **Cuando** se evalúa la imagen,
  * **Entonces** la app despliega un diálogo emergente: *"Advertencia de Iluminación: La imagen está demasiado oscura. Activa el flash de la cámara para estandarizar la iluminación"* y permite reintentar la toma.

---

### HU-04: Estimación de Hemoglobina y Clasificación OMS
* **Como** paciente o médico evaluador,
* **Quiero** ver la concentración estimada de hemoglobina ($g/dL$) clasificada según las guías clínicas de la OMS,
* **Para** identificar de inmediato si estoy en niveles normales, anemia leve, moderada o severa.

#### Criterios de Aceptación (BDD)
* **Escenario 4.1:** Clasificación para Mujer Adulta (Umbral OMS 12.0 g/dL)
  * **Dado que** el perfil está configurado como "Mujer Adulta".
  * **Cuando** la estimación de Hb resulta en $13.2\text{ g/dL}$,
  * **Entonces** el tacómetro se colorea de verde y reporta: *"Nivel Normal (Sin anemia)"*.
* **Escenario 4.2:** Detección de Anemia Crítica
  * **Dado que** el análisis arroja una hemoglobina $< 8.0\text{ g/dL}$,
  * **Entonces** la severidad se marca en rojo como *"Anemia Severa"* y se muestra la recomendación prioritaria de acudir de urgencia a un centro hospitalario.

---

### HU-05: Almacenamiento y Consulta de Historial Local
* **Como** usuario recurrente,
* **Quiero** acceder a la lista cronológica de todos mis escaneos pasados,
* **Para** verificar mi tendencia en el tiempo.

#### Criterios de Aceptación (BDD)
* **Escenario 5.1:** Registro automático tras finalizar análisis
  * **Cuando** se completa un análisis y se muestra la pantalla de resultados,
  * **Entonces** el registro se guarda en la base local (SharedPreferences/JSON) con fecha, hora, tipo de muestra, modelo usado y nivel de Hb.
* **Escenario 5.2:** Visualización en Historial
  * **Cuando** el usuario abre "Ver Historial y Evolución",
  * **Entonces** se muestran las tarjetas de resultados ordenadas de la más reciente a la más antigua.
* **Escenario 5.3:** Borrado seguro
  * **Cuando** el usuario pulsa el icono de papelera y confirma el borrado,
  * **Entonces** el historial se limpia por completo sin dejar residuos.


---

### HU-06: Calibración Óptima de Umbral de Decisión Youden (Sensibilidad Máxima)
* **Como** médico evaluador en campañas comunitarias,
* **Quiero** que el motor de inferencia utilice el umbral estadístico óptimo calibrado ( = \text{Sensibilidad} + \text{Especificidad} - 1$) en lugar del umbral neutro .50$,
* **Para** detectar con alta sensibilidad ($>90\%$) a pacientes con anemia limítrofe (.0 - 12.5\text{ g/dL}$) sin inflar los falsos positivos.

#### Criterios de Aceptación (BDD)
* **Escenario 6.1:** Umbral Youden para HemaViT ({\text{opt}} = 0.38$)
  * **Dado que** se procesa una muestra con el modelo HemaViT.
  * **Cuando** la probabilidad de anemia estimada es $\ge 0.38$,
  * **Entonces** el motor clasifica el caso como positivo para anemia, alcanzando un .00\%$ de sensibilidad y .39\%$ de especificidad.
* **Escenario 6.2:** Umbral Youden para MobileNetV3 ({\text{opt}} = 0.45$)
  * **Dado que** se procesa una muestra en un dispositivo de gama baja con MobileNetV3.
  * **Cuando** la probabilidad de anemia es $\ge 0.45$,
  * **Entonces** el motor activa la alerta diagnóstica, alcanzando .00\%$ de sensibilidad y .17\%$ de especificidad.

---

### HU-07: Protocolo Guiado Multi-Toma (Ensamble Binocular de 4 Fotos con Descarte de Outliers)
* **Como** profesional de salud o paciente realizando un escaneo clínico,
* **Quiero** contar con un modo guiado paso a paso para capturar 4 fotos binoculares (2 ojo derecho + 2 ojo izquierdo) con descarte automático de artefactos ópticos,
* **Para** minimizar el margen de error cromático por micro-movimientos o reflejos y obtener una estimación con menor varianza estadística.

#### Criterios de Aceptación (BDD)
* **Escenario 7.1:** Guía visual interactiva de 4 tomas
  * **Dado que** el usuario selecciona el modo "🔬 Clínico (4 Tomas)".
  * **Cuando** realiza las capturas,
  * **Entonces** la app indica el paso actual ("Toma 1: Ojo Der", "Toma 2: Ojo Der", "Toma 3: Ojo Izq", "Toma 4: Ojo Izq"), muestra los círculos numerados de avance y la tira de miniaturas con opción de eliminar cualquier toma borrosa.
* **Escenario 7.2:** Descarte automático de outliers ópticos ($N \ge 3$)
  * **Dado que** se capturan 4 fotos y una de ellas presenta un artefacto de brillo o sombra que desvía su Hb en $> 1.6\text{ g/dL}$ respecto a la mediana.
  * **Cuando** el motor ejecuta `analyzeMultiShot()`,
  * **Entonces** dicha toma se descarta del ensamble y las tomas restantes se promedian de manera robusta.
* **Escenario 7.3:** Estimación del Margen de Error Estándar (SE)
  * **Dado que** se completa el ensamble multi-toma.
  * **Entonces** la pantalla de resultados reporta la insignia `[🔬 Multi-Toma 4x]`, el margen $\pm\text{SE}\text{ g/dL}$ y un boost de confianza clínica.

---

### HU-08: Lenguaje Accesible y Guía Visual Fotográfica para No Expertos
* **Como** persona natural que realiza un chequeo en casa a sí misma o a un familiar sin formación médica,
* **Quiero** ver instrucciones fotográficas claras, advertencias de lo que se debe enfocar y terminología común en lugar de tecnicismos médicos,
* **Para** no cometer errores en la captura de la imagen y entender exactamente qué zona del cuerpo fotografiar.

#### Criterios de Aceptación (BDD)
* **Escenario 8.1:** Sustitución de jerga médica por términos cotidianos
  * **Dado que** el usuario se encuentra en la pantalla de escaneo.
  * **Entonces** las opciones anatómicas se muestran como *"Interior del Ojo (Párpado inferior)"* en vez de "Conjuntiva palpebral", y *"Uña de la Mano (Sin esmalte)"* en vez de "Lecho ungueal".
  * **Y** los modos de chequeo se presentan limpiamente como *"4 Fotos (Recomendado)"* y *"1 Foto (Rápido)"*.
* **Escenario 8.2:** Guía Visual Ilustrada Paso a Paso
  * **Cuando** el usuario pulsa el botón *"¿Cómo tomar la foto correctamente?"*,
  * **Entonces** se despliega un diálogo visual que explica:
    1. Para el párpado: mirar hacia arriba y bajar suavemente el párpado inferior con el dedo limpio para exponer la zona rosada/roja interior.
    2. Para la uña: mantener la uña limpia, sin esmalte, enfocando la superficie del dedo.
    3. Iluminación y enfoque: evitar sombras extremas y activar el flash si el entorno es tenue.

---

### HU-09: Guía Nutricional Interactiva de Alimentos Ricos en Hierro
* **Como** usuario con resultado de anemia leve o persona interesada en prevenir la deficiencia de hierro,
* **Quiero** consultar una guía práctica dentro de la app con alimentos ricos en hierro, potenciadores e inhibidores de absorción,
* **Para** saber qué alimentos incorporar a mi dieta y cómo combinarlos adecuadamente.

#### Criterios de Aceptación (BDD)
* **Escenario 9.1:** Acceso a la Guía de Nutrición
  * **Cuando** el usuario pulsa la tarjeta *"Alimentos con Hierro"* en la pantalla de inicio o en los resultados,
  * **Entonces** se abre el modal interactivo `NutritionGuideDialog`.
* **Escenario 9.2:** Clasificación y Consejos de Biodisponibilidad
  * **Dado que** el modal está visible,
  * **Entonces** se diferencian claramente:
    * **Hierro Hemo (Origen Animal):** Sangrecita, hígado, bazo, carnes rojas y pescados oscuros (alta tasa de absorción: 20-30%).
    * **Hierro No Hemo (Origen Vegetal):** Lentejas, espinacas, garbanzos y frutos secos (absorción: 3-8%).
    * **Potenciadores:** Alimentos con Vitamina C (cítricos, kiwi, pimiento) consumidos en la misma comida.
    * **Inhibidores:** Café, té negro y lácteos (evitar tomarlos inmediatamente tras la comida principal).

---

### HU-10: Compartir Resumen Diagnóstico con Profesionales de Salud
* **Como** usuario que acaba de realizarse un chequeo,
* **Quiero** compartir el reporte de mi resultado (nivel de hemoglobina, severidad y recomendaciones) mediante apps de mensajería o correo,
* **Para** enviarlo a mi médico de cabecera o a un familiar de manera rápida.

#### Criterios de Aceptación (BDD)
* **Escenario 10.1:** Lanzamiento del Intent Nativo de Android
  * **Dado que** el usuario está en la pantalla de resultados (`ResultScreen`).
  * **Cuando** presiona el botón *"Compartir Resumen"*,
  * **Entonces** el sistema dispara un `Intent.ACTION_SEND` (`text/plain`) con el selector nativo de Android.
* **Escenario 10.2:** Formato del Mensaje Compartido
  * **Entonces** el mensaje generado incluye: título de HemoScan AI, nivel de hemoglobina ($g/dL$), severidad, tipo de protocolo (1x o 4x), recomendaciones sugeridas y la aclaración de que es un screening preventivo no invasivo.

---

### HU-11: Gestión Granular del Historial y Sincronización en la Nube
* **Como** usuario particular,
* **Quiero** poder eliminar chequeos específicos individualmente además del borrado total, y contar con sincronización automática a la nube sólo cuando disponga de conexión estable a internet,
* **Para** mantener mi historial ordenado y tener respaldo en la nube sin gastar datos innecesariamente.

#### Criterios de Aceptación (BDD)
* **Escenario 11.1:** Eliminación Individual con Confirmación
  * **Dado que** el usuario consulta su historial (`HistoryScreen`).
  * **Cuando** pulsa el icono de papelera en una tarjeta específica,
  * **Entonces** aparece un diálogo de confirmación solicitando revalidar la acción; al confirmar, únicamente se elimina el elemento seleccionado manteniendo intactos los demás registros.
* **Escenario 11.2:** Detección de Conexión y Sincronización Inteligente (`CloudSyncManager`)
  * **Dado que** el dispositivo recupera conexión a internet estable (`NetworkCapabilities.NET_CAPABILITY_INTERNET`).
  * **Cuando** el usuario solicita respaldo en la nube o se detecta red activa,
  * **Entonces** los registros locales pendientes se sincronizan (`isSynced = true`) y las tarjetas del historial muestran la insignia `☁️ Nube` en vez de `📱 Local`.

---

### HU-12: Arquitectura Clean Code y Cero Fugas de Recursos (SonarQube Quality Gate)
* **Como** arquitecto de software y desarrollador de la aplicación,
* **Quiero** que el código fuente cumpla con los estándares de Clean Code de SonarQube (sin fugas en descriptores de archivos, operaciones thread-safe y modularidad UI),
* **Para** garantizar estabilidad máxima, cero bloqueos (ANR) y cero advertencias críticas en producción.

#### Criterios de Aceptación (BDD)
* **Escenario 12.1:** Cierre determinista de `AssetFileDescriptor` (`kotlin:S2095`)
  * **Dado que** el motor `AnemiaInferenceEngine` abre un modelo TFLite desde `assets`.
  * **Cuando** mapea el canal de memoria con `map(READ_ONLY, ...)`.
  * **Entonces** se ejecuta dentro de un bloque `.use { }` garantizando que el descriptor del archivo se cierre de inmediato sin fugas de memoria nativa.
* **Escenario 12.2:** Thread-Safety en `HistoryRepository` (`kotlin:S2885`)
  * **Dado que** múltiples corrutinas o hilos pueden invocar operaciones de guardado o lectura de historial.
  * **Entonces** los métodos críticos están sincronizados de forma atómica (`@Synchronized`) previniendo condiciones de carrera y serializaciones JSON redundantes.
* **Escenario 12.3:** Modularidad de Interfaz de Usuario (`kotlin:S3776` / `kotlin:S104`)
  * **Dado que** la pantalla de escaneo (`ScanScreen`) presentaba una alta complejidad cognitiva.
  * **Entonces** los selectores de modo y zonas anatómicas se extraen en componentes desacoplados (`ScanOptionsComponents.kt`) mejorando la mantenibilidad y legibilidad del código.

---

## 📋 Matriz de Trazabilidad y Estado de Validación

| Historia de Usuario | Módulo Responsable | Pruebas Unitarias / Instrumentadas | Validación Clínica / Calidad | Estado Final |
| :--- | :--- | :---: | :---: | :---: |
| **HU-01: Operación 100% Offline** | HistoryRepository, AnemiaInferenceEngine | ✅ AnemiaAppIntegrationTest | ✅ Verificado sin internet | **COMPLETADO** |
| **HU-02: Detección Adaptativa de Gama** | DeviceProfiler, DeviceTier | ✅ DeviceTierTest | ✅ Switch dinámico verificado | **COMPLETADO** |
| **HU-03: Control de Calidad de Luz** | ImagePreprocessor | ✅ ImagePreprocessorLogicTest | ✅ Filtro $Y \in [40, 245]$ | **COMPLETADO** |
| **HU-04: Criterios Clínicos OMS** | PatientProfile, DetectionResult | ✅ PatientProfileTest | ✅ 4 perfiles demográficos | **COMPLETADO** |
| **HU-05: Historial Offline Persistente** | HistoryRepository | ✅ AnemiaAppIntegrationTest | ✅ Almacenamiento JSON local | **COMPLETADO** |
| **HU-06: Calibración Youden** | AnemiaInferenceEngine | ✅ AnemiaAppIntegrationTest | ✅ AUC $95.64\%$ / $90.88\%$ | **COMPLETADO** |
| **HU-07: Protocolo Multi-Toma 4x** | ScanScreen, AnemiaInferenceEngine, DetectionResult | ✅ DetectionResultTest | ✅ Reducción de varianza 50% | **COMPLETADO** |
| **HU-08: Lenguaje Accesible y Guía Visual** | VisualCaptureGuideDialog, ScanOptionsComponents | ✅ Compose UI Testing | ✅ Usabilidad para público no experto | **COMPLETADO** |
| **HU-09: Guía Nutricional de Hierro** | NutritionGuideDialog, HomeScreen, ResultScreen | ✅ Compose UI Testing | ✅ Alimentos Hemo/No-Hemo y Vit C | **COMPLETADO** |
| **HU-10: Compartir Resumen Nativo** | ResultScreen (Intent.ACTION_SEND) | ✅ Android Intent Validation | ✅ Integración nativa del sistema | **COMPLETADO** |
| **HU-11: Borrado Granular & Cloud Sync** | HistoryScreen, CloudSyncManager | ✅ HistoryRepositoryTest | ✅ Sync adaptativo online/offline | **COMPLETADO** |
| **HU-12: Arquitectura Clean Code SonarQube** | AnemiaInferenceEngine, HistoryRepository, ScanScreen | ✅ Gradle testDebugUnitTest + SonarQube Audit | ✅ Quality Gate PASSED (0 deuda) | **COMPLETADO** |


