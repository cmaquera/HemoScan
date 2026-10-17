package com.anemia.detection

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.anemia.detection.engine.AnemiaInferenceEngine
import com.anemia.detection.engine.DeviceProfiler
import com.anemia.detection.engine.ImagePreprocessor
import com.anemia.detection.models.AnemiaSeverity
import com.anemia.detection.models.DeviceTier
import com.anemia.detection.models.PatientCategory
import com.anemia.detection.models.PatientProfile
import com.anemia.detection.storage.HistoryRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnemiaAppIntegrationTest {

    @Test
    fun testDeviceProfilerOnRealDevice() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val profiler = DeviceProfiler(appContext)
        val profile = profiler.getDeviceProfile()

        assertNotNull(profile)
        assertTrue("La memoria RAM detectada debe ser mayor a 0", profile.totalRamGB > 0)
        assertTrue("El número de núcleos de CPU debe ser mayor a 0", profile.cpuCores > 0)
        assertNotNull(profile.hardwareTier)
        assertNotNull(profile.activeModelName)
    }

    @Test
    fun testImagePreprocessingAndQualityCheck() {
        val preprocessor = ImagePreprocessor()

        // Crear bitmap simulado de tejido conjuntival con iluminación adecuada
        val testBitmap = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888)
        for (x in 0 until 224) {
            for (y in 0 until 224) {
                // Color rosado/rojo vascular típico
                testBitmap.setPixel(x, y, Color.rgb(180, 85, 75))
            }
        }

        val quality = preprocessor.assessQuality(testBitmap)
        assertTrue("La imagen con buena luz debe ser válida", quality.isValid)

        val preprocessed = preprocessor.preprocess(testBitmap)
        assertEquals(224, preprocessed.scaledBitmap.width)
        assertEquals(224, preprocessed.scaledBitmap.height)
        assertTrue(preprocessed.rednessRatio > 1.0)
        assertTrue(preprocessed.erythemaIndex > 0.0)
    }

    @Test
    fun testEndToEndInferenceAndHistorySaving() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val historyRepo = HistoryRepository(appContext)

        val testBitmap = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888)
        for (x in 0 until 224) {
            for (y in 0 until 224) {
                testBitmap.setPixel(x, y, Color.rgb(175, 80, 70))
            }
        }

        val profile = PatientProfile(category = PatientCategory.ADULT_FEMALE, age = 32)
        val result = AnemiaInferenceEngine(appContext).use { engine ->
            engine.analyze(
                bitmap = testBitmap,
                sampleType = "Conjuntiva Ocular",
                patientProfile = profile
            )
        }

        assertNotNull(result)
        assertTrue("El nivel de Hb debe estar en rango fisiológico", result.hemoglobinLevel in 5.0..18.0)
        assertTrue("La confianza debe ser alta", result.confidence >= 0.80)
        assertNotNull(result.severity)
        assertTrue("Debe incluir recomendaciones clínicas", result.recommendations.isNotEmpty())

        // Validar almacenamiento en la base de datos local sin dejar datos residuales
        try {
            historyRepo.saveResult(result)
            val history = historyRepo.getAllResults()
            assertTrue("El historial debe contener al menos el resultado recién guardado", history.isNotEmpty())
            assertEquals(result.id, history.first().id)
        } finally {
            historyRepo.deleteResult(result.id)
        }
    }
}
