package com.anemia.detection.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
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
        private const val TAG = "ImagePreprocessor"
        const val TARGET_SIZE = 224 // 224x224x3 para MobileNetV3 y Vision Transformer
        private const val PIXEL_CHANNELS = 3
        private const val MAX_DECODE_DIMENSION = 1024

        fun calculateLuminance(r: Int, g: Int, b: Int): Double {
            return 0.299 * r + 0.587 * g + 0.114 * b
        }

        fun calculateRednessRatio(meanR: Double, meanG: Double): Double {
            return if (meanG > 0) meanR / (meanG + 0.1) else 1.0
        }

        fun calculateErythemaIndex(meanR: Double, meanG: Double): Double {
            val safeR = max(1.0, meanR)
            val safeG = max(1.0, meanG)
            return ln(safeR) - ln(safeG)
        }

        fun evaluateLuminanceQuality(avgLum: Double): QualityReport {
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

        private fun calculateInSampleSize(width: Int, height: Int, maxDim: Int): Int {
            var inSampleSize = 1
            if (height > maxDim || width > maxDim) {
                val halfHeight = height / 2
                val halfWidth = width / 2
                while ((halfHeight / inSampleSize) >= maxDim && (halfWidth / inSampleSize) >= maxDim) {
                    inSampleSize *= 2
                }
            }
            return inSampleSize
        }
    }

    fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            val boundsOptions = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use { boundsStream ->
                BitmapFactory.decodeStream(boundsStream, null, boundsOptions)
            }

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = calculateInSampleSize(
                    boundsOptions.outWidth,
                    boundsOptions.outHeight,
                    MAX_DECODE_DIMENSION
                )
            }

            context.contentResolver.openInputStream(uri)?.use { decodeStream ->
                BitmapFactory.decodeStream(decodeStream, null, decodeOptions)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error decodificando Bitmap desde URI: ${e.message}")
            null
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

                totalLum += calculateLuminance(r, g, b)
                count++
            }
        }

        val avgLum = if (count > 0) totalLum / count else 0.0
        return evaluateLuminanceQuality(avgLum)
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
        val rednessRatio = calculateRednessRatio(meanR, meanG)
        val erythemaIndex = calculateErythemaIndex(meanR, meanG)
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
