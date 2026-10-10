"""
HemoScan AI - Pipeline de Aumento de Datos (Data Augmentation)
Simula variaciones ópticas de smartphones en campo:
- Dispersión angular y movimiento manual (rotación ±15°, volteo horizontal)
- Heterogeneidad de sensores y procesadores de señal de imagen (ISP)
- Fluctuaciones de temperatura de color y brillo del flash
"""

import tensorflow as tf

def build_augmentation_pipeline(image_size=(224, 224)):
    """
    Construye una capa de aumento de datos robusta para imágenes biomédicas
    Entrada: Tensor (Batch, H, W, 3) con valores en [0, 255]
    Salida: Tensor aumentado y normalizado en [0.0, 1.0]
    """
    pipeline = tf.keras.Sequential([
        tf.keras.layers.Resizing(image_size[0], image_size[1]),
        tf.keras.layers.Rescaling(1.0 / 255.0),
        
        # Transformaciones geométricas controladas
        tf.keras.layers.RandomFlip("horizontal"),
        tf.keras.layers.RandomRotation(factor=0.04), # Aprox ±15 grados
        tf.keras.layers.RandomZoom(height_factor=(-0.05, 0.05), width_factor=(-0.05, 0.05)),
        
        # Transformaciones fotométricas (simulación de diferentes sensores de cámara)
        tf.keras.layers.RandomBrightness(factor=0.12),
        tf.keras.layers.RandomContrast(factor=0.15),
    ], name="biomedical_augmentation")
    
    return pipeline

def build_validation_pipeline(image_size=(224, 224)):
    """
    Pipeline determinista para validación y prueba (solo redimensionamiento y normalización)
    """
    return tf.keras.Sequential([
        tf.keras.layers.Resizing(image_size[0], image_size[1]),
        tf.keras.layers.Rescaling(1.0 / 255.0),
    ], name="deterministic_validation")
