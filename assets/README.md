# Catálogo de Recursos Visuales — HemoScan AI

Este directorio centraliza los recursos de identidad visual y las capturas de pantalla reales actualizadas de la aplicación Android (`v0.0.5`), listas para documentación y construcción de páginas informativas (*landing page*).

La landing reconstruida está en [`../landing/`](../landing/README.md). Para actualizar sus imágenes WebP y la descarga del APK a partir de estos recursos, ejecuta `python scripts/prepare_landing.py` desde la raíz del repositorio. Los archivos originales de este catálogo se conservan intactos.

---

## 1. Identidad Visual (`assets/branding/`)

| Archivo | Dimensiones | Descripción |
| :--- | :--- | :--- |
| [`branding/app_icon.jpg`](branding/app_icon.jpg) | `1024 x 1024 px` | Logotipo e ícono oficial de alta resolución de HemoScan AI. |

---

## 2. Capturas de Pantalla Reales (`assets/screenshots/`)

Todas las capturas fueron obtenidas directamente desde un dispositivo físico **Motorola Edge 60 Fusion** (`Android 16 / SDK 36`, resolución nativa `1220 x 2712 px`) ejecutando la interfaz actualizada con inferencia local.

| Archivo | Vista de la Aplicación | Contenido y Uso Sugerido en Landing |
| :--- | :--- | :--- |
| [`screenshots/01-home-dashboard.png`](screenshots/01-home-dashboard.png) | Inicio (Parte superior) | Encabezado con nuevo logotipo, tarjeta principal *"Test Preliminar de Anemia — Iniciar Chequeo Ahora"* y resumen del último chequeo con escala cromática continua. Ideal para sección **Hero**. |
| [`screenshots/02-home-guides-actions.png`](screenshots/02-home-guides-actions.png) | Inicio (Guías y accesos) | Carrusel *"Cómo tomar una foto clara"* y tarjetas niveladas de *Alimentos con Hierro* y *Compartir Resumen*. |
| [`screenshots/03-visual-capture-guide.png`](screenshots/03-visual-capture-guide.png) | Modal Guía Fotográfica | Guía interactiva paso a paso para enfocar el párpado inferior o la uña sin conocimientos médicos, con indicadores *Correcto / Evitar*. Ideal para sección **Cómo funciona**. |
| [`screenshots/04-nutrition-iron-guide.png`](screenshots/04-nutrition-iron-guide.png) | Modal Guía Nutricional | Catálogo práctico de alimentos ricos en hierro hemo, no hemo y recomendación de absorción con vitamina C. Ideal para sección **Bienestar y acompañamiento**. |
| [`screenshots/05-scan-protocol.png`](screenshots/05-scan-protocol.png) | Nuevo Chequeo (Protocolo) | Selección entre *Modo Guiado (4 fotos)* y *Modo Rápido (1 foto)*, junto con selector anatómico (*Interior del Ojo* / *Uña de la Mano*). |
| [`screenshots/06-scan-samples-loaded.png`](screenshots/06-scan-samples-loaded.png) | Secuencia de 4 Fotos Lista | Vista del protocolo guiado completo (`4/4` tomas verificadas) lista para ejecutar el análisis local. |
| [`screenshots/07-result-detail.png`](screenshots/07-result-detail.png) | Resultado (Alerta Moderada) | Tacómetro radial de hemoglobina (`10.7 g/dL`), clasificación según umbral OMS y rango saludable de referencia. |
| [`screenshots/08-result-recommendations.png`](screenshots/08-result-recommendations.png) | Resultado (Pasos sugeridos) | Detalle de calidad técnica de captura (`4 capturas consolidadas`) y acciones preventivas recomendadas. |
| [`screenshots/09-history-records.png`](screenshots/09-history-records.png) | Historial Offline | Lista cronológica de chequeos almacenados de forma 100% privada en el teléfono, con etiquetas de motor IA (`HemaViT`) y número de tomas. |
| [`screenshots/10-settings-ai-engine.png`](screenshots/10-settings-ai-engine.png) | Configuración (OMS y Resumen IA) | Selector demográfico de umbrales OMS (Hombre, Mujer, Gestante, Niño/Adolescente) y tarjeta de rendimiento local (`3.28 GFLOPs`, `~32 ms`, `100% Offline`). |
| [`screenshots/11-settings-hardware-models.png`](screenshots/11-settings-hardware-models.png) | Diagnóstico de Hardware y Modelos | Detección real del dispositivo (`motorola edge 60 fusion`, `7.2 GB RAM`, `8 núcleos`) y conmutador entre modo Automático, `MobileNetV3`, `HemaViT` y Motor Clínico. |
| [`screenshots/12-result-normal-healthy.png`](screenshots/12-result-normal-healthy.png) | Resultado (Nivel Óptimo) | Tacómetro radial en zona verde óptima (`13.8 g/dL — Nivel Óptimo / Valores Normales`) mediante escaneo de uña. Alternativa positiva ideal para **Hero** o comparativas visuales. |

---

## 3. Binario y Documentación Relacionada

* **APK de prueba actualizado (`v0.0.5`):** [`../releases/HemoScan_AI_debug.apk`](../releases/HemoScan_AI_debug.apk)
* **Aviso médico y exención de responsabilidad:** [`../docs/DISCLAIMER.md`](../docs/DISCLAIMER.md)
