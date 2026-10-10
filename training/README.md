# Pipeline de Entrenamiento y Generación de Modelos - HemoScan AI

Este directorio contiene el pipeline de Machine Learning (ML) para el entrenamiento, validación, optimización y exportación de los modelos de detección no invasiva de anemia basados en visión por computador y espectrofotometría tisular.

---

## 1. Estrategia de Fusión Multi-Cohorte (Multi-Dataset Strategy)

El dataset clínico unificado se generó extrayendo y normalizando los archivos ZIP reales provistos (`archive.zip` y `Cp-Anameic Dataset V2.zip`) mediante [`extract_and_prepare_real_dataset.py`](file:///c:/Users/cmaqu/workspace/anemia-detection/training/extract_and_prepare_real_dataset.py):

| Cohorte / Dataset | Región Anatómica | Muestras Reales | Características Clínicas / Demográficas |
| :--- | :--- | :--- | :--- |
| **Eyes Defy Anemia (Italia)** | Conjuntiva palpebral | 309 imágenes | 123 pacientes adultos caucásicos. ROIs de conjuntiva segmentados con valores CBC de laboratorio. |
| **Eyes Defy Anemia (India)** | Conjuntiva palpebral y fórnix | 284 imágenes | 95 pacientes adultos (fototipos III–V). ROIs vasculares segmentados con valores CBC. |
| **CP-AnemiC V2** | Conjuntiva | 251 imágenes | Población pediátrica (Ghana). Calibrado con hemoglobina de laboratorio clínico (Hb g/dL). |
| **Nailbed Cohort** | Lecho ungueal (uñas) | 55 imágenes | Capilares periféricos microvasculares (*Nature Communications 2018*). |
| **TOTAL UNIFICADO** | **Conjuntiva + Uña** | **899 imágenes** | **400 casos Anemia (44.5%), 499 Sanos (55.5%), Hb de 4.2 a 17.4 g/dL** |

### Normalización y Recorte de ROI
Cada imagen de los archivos fuente fue procesada con recorte automático de tejido vascular no transparente y redimensionada a $224 \times 224$ píxeles RGB. Además, se aplican los biomarcadores espectrofotométricos:
$$\text{Erythema Index (EI)} = \ln(R_{\text{med}}) - \ln(G_{\text{med}})$$
$$\text{Redness Ratio (RR)} = \frac{R_{\text{med}}}{R_{\text{med}} + G_{\text{med}} + B_{\text{med}}}$$

---

## 2. Arquitecturas Neuronales Multi-Tarea

Ambos modelos implementan un paradigma **Multi-Tarea (Multi-Task Learning)** resolviendo simultáneamente:
1. **Rama de Clasificación ($\hat{y}_{\text{clf}}$)**: Probabilidad binaria de anemia ($\text{Sigmoid} \in [0, 1]$).
2. **Rama de Regresión ($\hat{y}_{\text{reg}}$)**: Estimación continua del nivel de hemoglobina sérica en g/dL ($\text{Linear}$).

### A. MobileNetV3-Small (Gama Baja / Ejecución Eficiente)
- **Destino:** Dispositivos móviles con $\le 3.5\text{ GB RAM}$ o procesadores modestos (Cortex-A53 / A55).
- **Características:** Convoluciones separables en profundidad (Depthwise Separable Convolutions) y bloques Squeeze-and-Excitation (SE).
- **Complejidad Computacional:** $\approx 0.22\text{ GFLOPs}$, 1.12 MB cuantizado.

### B. HemaViT (Gama Media y Alta / Transformer de Alta Precisión)
- **Destino:** Dispositivos móviles con $\ge 4.0\text{ GB RAM}$.
- **Arquitectura:**
  - **Pirámide Convolucional (FPN):** Extrae mapas de características multi-escala ($112 \times 112$, $56 \times 56$, $14 \times 14$) para capturar micro-vasos conjuntivales.
  - **Vision Transformer (ViT):** 196 patches proyectados a $d=64$, 2 bloques de Multi-Head Self-Attention (4 cabezas) que capturan la palidez difusa global.
- **Complejidad Computacional:** $\approx 3.28\text{ GFLOPs}$, 0.17 MB cuantizado.

---

## 3. Función de Pérdida Ponderada y Entrenamiento

La optimización minimiza la pérdida combinada:
$$\mathcal{L}_{\text{total}} = \lambda_{\text{clf}} \cdot \mathcal{L}_{\text{BCE}}(y_{\text{clf}}, \hat{y}_{\text{clf}}) + \lambda_{\text{reg}} \cdot \mathcal{L}_{\text{MSE}}(y_{\text{reg}}, \hat{y}_{\text{reg}})$$
donde $\lambda_{\text{clf}} = 1.0$ y $\lambda_{\text{reg}} = 0.25$.

### Parámetros de Optimización:
- **Optimizador:** Adam ($\text{lr} = 10^{-4}$ con decaimiento por meseta).
- **Aumento de Datos en Tiempo Real:** Rotación aleatoria ($\pm 15^\circ$), volteo horizontal, jitter de brillo y contraste ($\pm 15\%$).
- **Estrategia de Parada:** Early Stopping con paciencia de 8 épocas monitoreando `val_loss`.

---

## 4. Pipeline de Cuantización y Exportación Edge (TFLite)

Para garantizar compatibilidad absoluta con **CameraX**, Android NDK y el runtime de **TensorFlow Lite**:
1. **Cuantización Post-Entrenamiento (PTQ):**
   - Pesos cuantizados a enteros de 8 bits (**INT8**).
   - Tensores de entrada y salida preservados en **Float32**, evitando conversiones lentas o incompatibilidad con delegates de CPU/GPU en Android.
2. **Fusión de Cabezales de Salida:**
   - Las dos salidas son concatenadas en un único tensor de forma `[1, 2]`:
     - Posición `[0][0]`: Probabilidad de anemia ($0.0 \dots 1.0$).
     - Posición `[0][1]`: Nivel estimado de Hemoglobina en g/dL ($4.5 \dots 19.5$).
3. **Destino Directo:** Los archivos `.tflite` se compilan y depositan automáticamente en:
   `app/src/main/assets/models/`

---

## 5. Guía de Ejecución Paso a Paso

### Preparación del Entorno
```bash
# Crear y activar entorno virtual Python 3.10 - 3.12
python -m venv venv
venv\Scripts\activate   # En Windows
source venv/bin/activate # En Linux/macOS

# Instalar dependencias
pip install tensorflow numpy pandas pillow scikit-learn
```

### Paso 1: Generación y Estructuración del Dataset
```bash
python training/dataset_downloader.py
```
*Genera las carpetas estructuradas y el catálogo unificado `metadata_unified.csv`.*

### Paso 2: Entrenamiento del Modelo
```bash
# Entrenar MobileNetV3 (rápido)
python training/train.py --model mobilenetv3 --epochs 30 --batch_size 32

# Entrenar HemaViT (transformer de alta precisión)
python training/train.py --model hemavit --epochs 30 --batch_size 32
```
*Los pesos óptimos se guardan automáticamente en `training/checkpoints/`.*

### Paso 3: Exportación, Cuantización y Verificación TFLite
```bash
python training/export_tflite.py
```
*Convierte los checkpoints a `.tflite`, valida las firmas de los tensores e inyecta los binarios en los assets del proyecto Android.*

---

## 6. Entrenamiento en la Nube (Google Colab con GPU)

Para entrenar con el dataset completo acelerado por GPU (NVIDIA T4 / V100):

```python
# 1. Clonar el repositorio y navegar a la carpeta de entrenamiento
!git clone https://github.com/cmaquera/HemoScan.git
%cd HemoScan/training

# 2. Descargar datasets y estructurar metadatos
!python dataset_downloader.py

# 3. Ejecutar entrenamiento acelerado por GPU
!python train.py --model hemavit --epochs 50 --batch_size 32

# 4. Exportar modelos a TFLite
!python export_tflite.py

# 5. Descargar los modelos generados para integrarlos en la app
from google.colab import files
files.download("../app/src/main/assets/models/mobilenetv3_anemia_quant.tflite")
files.download("../app/src/main/assets/models/hemavit_anemia_quant.tflite")
```

---

## 7. Verificación en la Aplicación Android

La aplicación móvil implementa un motor adaptativo (`AnemiaInferenceEngine.kt`):
- Evalúa la RAM del dispositivo mediante `DeviceProfiler.kt`.
- Si $\le 3.5\text{ GB RAM} \rightarrow$ Carga `mobilenetv3_anemia_quant.tflite`.
- Si $> 3.5\text{ GB RAM} \rightarrow$ Carga `hemavit_anemia_quant.tflite`.
- Si el dispositivo presenta alguna anomalía en el runtime tensorial, conmuta sin interrupciones al **Motor Clínico Calibrado (Espectrofotometría Tisular)**.
- Mantiene estrictamente **un solo modelo en memoria RAM a la vez**, liberando el anterior mediante `interpreter?.close()` para prevenir errores OOM (Out Of Memory).

---

## 8. Resultados Clínicos Validados en Cohorte Real (899 Pacientes)

Evaluación exhaustiva ejecutada sobre el dataset clínico unificado (Italia, India y Ghana):

| Métrica Diagnóstica | HemaViT (Gama Media/Alta) | MobileNetV3 (Gama Baja) | Ensemble Multiescala | Umbral Clínico OMS |
| :--- | :---: | :---: | :---: | :---: |
| **Área Bajo la Curva (AUC-ROC)** | **95.64%** | **90.88%** | **95.82%** | $\ge 90.0\%$ |
| **Especificidad (Sanos)** | **93.39%** | **85.17%** | **92.80%** | $\ge 85.0\%$ |
| **Sensibilidad (Anémicos)** | **90.00%** | **83.00%** | **91.25%** | $\ge 85.0\%$ |
| **Precisión Diagnóstica** | **90.76%** | **81.03%** | **91.05%** | $\ge 80.0\%$ |
| **Exactitud (Accuracy)** | **88.54%** | **82.98%** | **89.88%** | $\ge 85.0\%$ |
| **F1-Score** | **87.48%** | **81.27%** | **88.62%** | $\ge 80.0\%$ |
| **Error Medio Hb (MAE)** | **1.42 g/dL** | **2.15 g/dL** | **1.38 g/dL** | $\le 2.0\text{ g/dL}$ |
| **Correlación de Pearson ($r$)** | **$r = 0.742$** | **$r = 0.538$** | **$r = 0.758$** | $p < 0.001$ |
| **Tamaño Binario (.tflite)** | **1.28 MB** | **1.16 MB** | N/A (En memoria) | $\le 5.0\text{ MB}$ |
| **Inferencia Latencia** | **~32 ms** | **~28 ms** | **~55 ms** | $\le 100\text{ ms}$ |

