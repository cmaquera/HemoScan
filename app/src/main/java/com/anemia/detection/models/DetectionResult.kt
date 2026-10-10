package com.anemia.detection.models

data class DetectionResult(
    val id: String = System.currentTimeMillis().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val isAnemic: Boolean,
    val hemoglobinLevel: Double,
    val confidence: Double,
    val modelUsed: String,
    val inferenceTimeMs: Long,
    val sampleType: String,
    val severity: AnemiaSeverity,
    val rednessRatio: Double,
    val erythemaIndex: Double,
    val imageUri: String? = null,
    val recommendations: List<String>,
    val inferenceMethod: String = "Modelo Neuronal (TFLite)",
    val shotCount: Int = 1,
    val standardError: Double? = null,
    val isSynced: Boolean = false
) {
    val isMultiShot: Boolean get() = shotCount > 1

    val isNeuralInference: Boolean
        get() = when {
            inferenceMethod.contains("Motor Clínico") -> false
            inferenceMethod.contains("Modelo Neuronal") -> true
            else -> modelUsed.contains("HemaViT") || modelUsed.contains("MobileNetV3")
        }
}
