package com.anemia.detection

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.ln
import kotlin.math.max

class ImagePreprocessorLogicTest {

    @Test
    fun testLuminanceFormula() {
        // Fórmula ITU-R BT.601: Y = 0.299*R + 0.587*G + 0.114*B
        val r = 200
        val g = 150
        val b = 100

        val expectedLuminance = 0.299 * r + 0.587 * g + 0.114 * b
        assertEquals(159.25, expectedLuminance, 0.01)

        // Validación de umbrales clínicos
        val darkLuminance = 0.299 * 20 + 0.587 * 20 + 0.114 * 20 // 20.0 (Muy oscuro)
        assertTrue(darkLuminance < 40.0)

        val brightLuminance = 0.299 * 250 + 0.587 * 250 + 0.114 * 250 // 250.0 (Sobreexpuesto)
        assertTrue(brightLuminance > 245.0)
    }

    @Test
    fun testErythemaIndexCalculation() {
        // Índice de Eritema (EI) = ln(Rojo) - ln(Verde)
        val healthyConjunctivaR = 190.0
        val healthyConjunctivaG = 80.0
        val healthyEI = ln(healthyConjunctivaR) - ln(healthyConjunctivaG)

        val anemicConjunctivaR = 140.0 // Tejido pálido con menos vascularización
        val anemicConjunctivaG = 120.0
        val anemicEI = ln(anemicConjunctivaR) - ln(anemicConjunctivaG)

        // El tejido sano bien vascularizado debe tener un índice de eritema mayor que el tejido anémico pálido
        assertTrue("El eritema sano debe ser mayor que el anémico", healthyEI > anemicEI)
        assertTrue(healthyEI > 0.8)
        assertTrue(anemicEI < 0.2)
    }

    @Test
    fun testRednessRatioCalculation() {
        // Redness Ratio = R / (G + 0.1)
        val r = 180.0
        val g = 90.0
        val ratio = r / (g + 0.1)

        assertTrue(ratio > 1.9)
    }
}
