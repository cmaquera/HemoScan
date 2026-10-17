package com.anemia.detection

import com.anemia.detection.engine.ImagePreprocessor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ImagePreprocessorLogicTest {

    @Test
    fun testLuminanceFormulaAndQualityGate() {
        // Fórmula ITU-R BT.601 ejecutada en el SUT: Y = 0.299*R + 0.587*G + 0.114*B
        val expectedLuminance = ImagePreprocessor.calculateLuminance(200, 150, 100)
        assertEquals(159.25, expectedLuminance, 0.01)

        val validReport = ImagePreprocessor.evaluateLuminanceQuality(expectedLuminance)
        assertTrue(validReport.isValid)
        assertTrue(validReport.flashLikelyUsed)

        // Validación de umbrales clínicos en el SUT
        val darkLuminance = ImagePreprocessor.calculateLuminance(20, 20, 20)
        assertTrue(darkLuminance < 40.0)
        val darkReport = ImagePreprocessor.evaluateLuminanceQuality(darkLuminance)
        assertFalse(darkReport.isValid)

        val brightLuminance = ImagePreprocessor.calculateLuminance(250, 250, 250)
        assertTrue(brightLuminance > 245.0)
        val brightReport = ImagePreprocessor.evaluateLuminanceQuality(brightLuminance)
        assertFalse(brightReport.isValid)
    }

    @Test
    fun testErythemaIndexCalculation() {
        // Índice de Eritema (EI) = ln(Rojo) - ln(Verde) mediante ImagePreprocessor
        val healthyEI = ImagePreprocessor.calculateErythemaIndex(190.0, 80.0)
        val anemicEI = ImagePreprocessor.calculateErythemaIndex(140.0, 120.0)

        assertTrue("El eritema sano debe ser mayor que el anémico", healthyEI > anemicEI)
        assertTrue(healthyEI > 0.8)
        assertTrue(anemicEI < 0.2)
    }

    @Test
    fun testRednessRatioCalculation() {
        val ratio = ImagePreprocessor.calculateRednessRatio(180.0, 90.0)
        assertTrue(ratio > 1.9)
        assertEquals(1.0, ImagePreprocessor.calculateRednessRatio(100.0, 0.0), 0.001)
    }
}
