package com.anemia.detection.models

enum class DeviceTier(val displayName: String, val recommendedModel: String) {
    LOW_END("Gama Baja / Económica", "MobileNetV3 (INT8 Cuantizado)"),
    MID_HIGH_END("Gama Media / Alta", "HemaViT (Vision Transformer + FPN)")
}

enum class ModelSelectionMode(val title: String, val description: String) {
    AUTOMATIC(
        "Automático (Recomendado)",
        "Detecta la RAM y procesador del teléfono para seleccionar el modelo óptimo sin congelar la app."
    ),
    FORCE_MOBILENET_V3(
        "MobileNetV3 (Modelo Neuronal Ligero)",
        "Red neuronal convolucional INT8 (0.22 GFLOPs, 1.16 MB). Inferencia ultra rápida."
    ),
    FORCE_HEMA_VIT(
        "HemaViT (Modelo Neuronal Transformer)",
        "Red neuronal Vision Transformer + FPN (3.28 GFLOPs, 1.28 MB, 95.6% AUC). Máxima precisión."
    ),
    FORCE_CALIBRATED_ENGINE(
        "Motor Clínico Calibrado (Espectrofotometría)",
        "Algoritmo biofísico de palidez tisular y eritema óptico (Nature Communications, 2018)."
    )
}

data class DeviceInfoProfile(
    val totalRamGB: Double,
    val isLowRamDevice: Boolean,
    val cpuCores: Int,
    val deviceModel: String,
    val osVersion: String,
    val hardwareTier: DeviceTier,
    val selectionMode: ModelSelectionMode
) {
    val isForcedCalibratedEngine: Boolean
        get() = selectionMode == ModelSelectionMode.FORCE_CALIBRATED_ENGINE

    val isRunningLightweight: Boolean
        get() = when (selectionMode) {
            ModelSelectionMode.FORCE_MOBILENET_V3 -> true
            ModelSelectionMode.FORCE_HEMA_VIT -> false
            ModelSelectionMode.FORCE_CALIBRATED_ENGINE -> true
            ModelSelectionMode.AUTOMATIC -> hardwareTier == DeviceTier.LOW_END
        }

    val activeModelName: String
        get() = when (selectionMode) {
            ModelSelectionMode.FORCE_MOBILENET_V3 -> "MobileNetV3 (Modo Rápido)"
            ModelSelectionMode.FORCE_HEMA_VIT -> "HemaViT (Alta Precisión)"
            ModelSelectionMode.FORCE_CALIBRATED_ENGINE -> "Motor Clínico Calibrado"
            ModelSelectionMode.AUTOMATIC -> {
                if (hardwareTier == DeviceTier.LOW_END) {
                    "MobileNetV3 (Auto: Gama Baja)"
                } else {
                    "HemaViT (Auto: Gama Media/Alta)"
                }
            }
        }
}
