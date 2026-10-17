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
    val isMultiShot: Boolean
        get() {
            val model = (modelUsed as String?) ?: ""
            return shotCount > 1 || model.contains("Multi-Toma")
        }

    val isNeuralInference: Boolean
        get() {
            val method = (inferenceMethod as String?) ?: ""
            val model = (modelUsed as String?) ?: ""
            return when {
                method.contains("Motor Clínico") -> false
                method.contains("Modelo Neuronal") -> true
                else -> model.contains("HemaViT") || model.contains("MobileNetV3") || method.isEmpty()
            }
        }

    /**
     * Normaliza instancias deserializadas por Gson desde versiones previas del esquema JSON,
     * donde la reflexión de Java puede dejar propiedades no-nulas de Kotlin en null o en 0.
     */
    @Suppress("USELESS_ELVIS")
    fun normalized(fallbackIndex: Int = 0): DetectionResult {
        val safeTimestamp = if (timestamp > 0L) timestamp else System.currentTimeMillis()
        val safeId = (id as String?)?.takeIf { it.isNotBlank() } ?: "${safeTimestamp}_$fallbackIndex"
        val safeModel = (modelUsed as String?)?.takeIf { it.isNotBlank() } ?: "HemaViT (Transformer Alta Precisión)"
        val safeMethod = (inferenceMethod as String?)?.takeIf { it.isNotBlank() }
            ?: if (safeModel.contains("Calibración") || safeModel.contains("Clínico")) {
                "Motor Clínico Calibrado (Espectrofotometría)"
            } else {
                "Modelo Neuronal (TFLite)"
            }
        val safeSample = (sampleType as String?)?.takeIf { it.isNotBlank() } ?: "Interior del Ojo"
        val safeSeverity = (severity as AnemiaSeverity?) ?: if (isAnemic) AnemiaSeverity.MILD else AnemiaSeverity.NORMAL
        val alignedSeverity = if (isAnemic && safeSeverity == AnemiaSeverity.NORMAL) {
            AnemiaSeverity.MILD
        } else {
            safeSeverity
        }
        val safeRecommendations = ((recommendations as List<String?>?)?.filterNotNull())
            ?: emptyList()
        val safeShotCount = when {
            shotCount > 0 -> shotCount
            safeModel.contains("Multi-Toma") -> 4
            else -> 1
        }
        val safeRedness = if (rednessRatio > 5.0) {
            ((rednessRatio / 10.0) * 100.0).toInt() / 100.0
        } else {
            rednessRatio
        }

        return copy(
            id = safeId,
            timestamp = safeTimestamp,
            modelUsed = safeModel,
            inferenceMethod = safeMethod,
            sampleType = safeSample,
            severity = alignedSeverity,
            isAnemic = alignedSeverity != AnemiaSeverity.NORMAL,
            recommendations = safeRecommendations,
            shotCount = safeShotCount,
            rednessRatio = safeRedness
        )
    }
}
