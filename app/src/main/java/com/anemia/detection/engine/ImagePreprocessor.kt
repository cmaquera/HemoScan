package com.anemia.detection.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.ln
import kotlin.math.max

data class QualityReport(
    val isValid: Boolean,
    val message: String,
    val averageLuminance: Double,
    val flashLikelyUsed: Boolean
)

data class PreprocessedData(
    val scaledBitmap: Bitmap,
    val byteBuffer: ByteBuffer,
    val rednessRatio: Double,
    val erythemaIndex: Double,
    val averageLuminance: Double
)

class ImagePreprocessor {

    companion object {
        const val TARGET_SIZE = 224 // 224x224x3 para MobileNetV3 y Vision Transformer
        private const val PIXEL_CHANNELS = 3
    }

    fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        var input: InputStream? = null
        return try {
            input = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            null
        } finally {
            input?.close()
        }
    }

    /**
     * Evalúa rápidamente si la foto tiene suficiente iluminación y contraste
     * para asegurar reproducibilidad clínica en móviles de gama baja.
     */
    fun assessQuality(bitmap: Bitmap): QualityReport {
        var totalLum = 0.0
        var count = 0

        val stepX = max(1, bitmap.width / 40)
        val stepY = max(1, bitmap.height / 40)

        for (y in 0 until bitmap.height step stepY) {
            for (x in 0 until bitmap.width step stepX) {
                val pixel = bitmap.getPixel(x, y)
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF

                // Luminancia estándar Y = 0.299R + 0.587G + 0.114B
                val lum = 0.299 * r + 0.587 * g + 0.114 * b
                totalLum += lum
                count++
            }
        }

        val avgLum = if (count > 0) totalLum / count else 0.0

        return when {
            avgLum < 40.0 -> QualityReport(
                isValid = false,
                message = "La imagen está demasiado oscura. Activa el flash de la cámara para estandarizar la iluminación.",
                averageLuminance = avgLum,
                flashLikelyUsed = false
            )
            avgLum > 245.0 -> QualityReport(
                isValid = false,
                message = "La imagen está sobreexpuesta o con brillo excesivo. Evita apuntar a fuentes de luz directa.",
                averageLuminance = avgLum,
                flashLikelyUsed = true
            )
            else -> QualityReport(
                isValid = true,
                message = "Iluminación adecuada para análisis de biomarcadores.",
                averageLuminance = avgLum,
                flashLikelyUsed = avgLum > 90.0
            )
        }
    }

    /**
     * Redimensiona a 224x224, construye el ByteBuffer normalizado para TFLite
     * y calcula los coeficientes cromáticos (Índice de Eritema y R/G Ratio).
     */
    fun preprocess(bitmap: Bitmap): PreprocessedData {
        val scaled = Bitmap.createScaledBitmap(bitmap, TARGET_SIZE, TARGET_SIZE, true)

        // Buffer directo en memoria nativa para evitar copias lentas de la JVM
        val byteBuffer = ByteBuffer.allocateDirect(1 * TARGET_SIZE * TARGET_SIZE * PIXEL_CHANNELS * 4)
        byteBuffer.order(ByteOrder.nativeOrder())

        var sumR = 0.0
        var sumG = 0.0
        var sumB = 0.0
        val totalPixels = TARGET_SIZE * TARGET_SIZE

        val pixels = IntArray(totalPixels)
        scaled.getPixels(pixels, 0, TARGET_SIZE, 0, 0, TARGET_SIZE, TARGET_SIZE)

        for (pixel in pixels) {
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF

            sumR += r
            sumG += g
            sumB += b

            // Normalización [0.0f, 1.0f] requerida por la entrada del modelo
            byteBuffer.putFloat(r / 255.0f)
            byteBuffer.putFloat(g / 255.0f)
            byteBuffer.putFloat(b / 255.0f)
        }

        val meanR = sumR / totalPixels
        val meanG = sumG / totalPixels
        val meanB = sumB / totalPixels

        // Biomarcadores cromáticos cuantitativos
        val rednessRatio = if (meanG > 0) meanR / (meanG + 0.1) else 1.0
        val safeR = max(1.0, meanR)
        val safeG = max(1.0, meanG)
        val erythemaIndex = ln(safeR) - ln(safeG)
        val avgLum = 0.299 * meanR + 0.587 * meanG + 0.114 * meanB

        return PreprocessedData(
            scaledBitmap = scaled,
            byteBuffer = byteBuffer,
            rednessRatio = rednessRatio,
            erythemaIndex = erythemaIndex,
            averageLuminance = avgLum
        )
    }
}
