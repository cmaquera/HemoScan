package com.anemia.detection

import com.anemia.detection.models.AnemiaSeverity
import com.anemia.detection.models.DetectionResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DetectionResultTest {

    @Test
    fun testDetectionResultCreationAndIntegrity() {
        val result = DetectionResult(
            id = "test-123",
            timestamp = 1700000000000L,
            isAnemic = true,
            hemoglobinLevel = 9.8,
            confidence = 0.96,
            modelUsed = "MobileNetV3 (Gama Baja / Rápido)",
            inferenceTimeMs = 28L,
            sampleType = "Conjuntiva Ocular",
            severity = AnemiaSeverity.MODERATE,
            rednessRatio = 1.34,
            erythemaIndex = 0.312,
            imageUri = null,
            recommendations = listOf(
                "Nivel estimado de hemoglobina notablemente disminuido.",
                "Es prioritario acudir a un centro de salud para estudio etiológico y confirmación."
            )
        )

        assertEquals("test-123", result.id)
        assertTrue(result.isAnemic)
        assertEquals(9.8, result.hemoglobinLevel, 0.001)
        assertEquals(AnemiaSeverity.MODERATE, result.severity)
        assertEquals("Modelo Neuronal (TFLite)", result.inferenceMethod)
        assertTrue(result.isNeuralInference)
        assertEquals(2, result.recommendations.size)
        assertNotNull(result.recommendations.first())

        val clinicalResult = result.copy(
            inferenceMethod = "Motor Clínico Calibrado (Espectrofotometría Tisular)"
        )
        org.junit.Assert.assertFalse(clinicalResult.isNeuralInference)
        org.junit.Assert.assertFalse(result.isMultiShot)
        assertEquals(1, result.shotCount)

        val multiShotResult = result.copy(
            shotCount = 4,
            standardError = 0.24
        )
        assertTrue(multiShotResult.isMultiShot)
        assertEquals(4, multiShotResult.shotCount)
        assertEquals(0.24, multiShotResult.standardError ?: 0.0, 0.001)
    }
}
