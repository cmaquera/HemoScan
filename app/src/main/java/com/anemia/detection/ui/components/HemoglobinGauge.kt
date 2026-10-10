package com.anemia.detection.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anemia.detection.models.AnemiaSeverity
import com.anemia.detection.theme.ClinicalCardLowest
import com.anemia.detection.theme.ClinicalOnSurface
import com.anemia.detection.theme.ClinicalPrimary
import com.anemia.detection.theme.ClinicalTextSecondary
import com.anemia.detection.theme.SeverityMild
import com.anemia.detection.theme.SeverityMildContainer
import com.anemia.detection.theme.SeverityModerate
import com.anemia.detection.theme.SeverityNormal
import com.anemia.detection.theme.SeverityNormalContainer
import com.anemia.detection.theme.SeveritySevere
import com.anemia.detection.theme.SeveritySevereContainer
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HemoglobinGauge(
    hemoglobinLevel: Double,
    severity: AnemiaSeverity,
    whoThreshold: Double,
    sampleType: String = "Conjuntiva Ocular",
    modifier: Modifier = Modifier
) {
    val severityColor = when (severity) {
        AnemiaSeverity.NORMAL -> SeverityNormal
        AnemiaSeverity.MILD -> SeverityMild
        AnemiaSeverity.MODERATE -> SeverityModerate
        AnemiaSeverity.SEVERE -> SeveritySevere
    }

    val severityBgColor = when (severity) {
        AnemiaSeverity.NORMAL -> SeverityNormalContainer
        AnemiaSeverity.MILD -> SeverityMildContainer
        AnemiaSeverity.MODERATE -> SeveritySevereContainer
        AnemiaSeverity.SEVERE -> SeveritySevereContainer
    }

    val severityBadgeText = when (severity) {
        AnemiaSeverity.NORMAL -> "Nivel Óptimo (Valores Normales)"
        AnemiaSeverity.MILD -> "Nivel Levemente Bajo (Posible Anemia Leve)"
        AnemiaSeverity.MODERATE -> "Nivel Moderadamente Bajo (Anemia Moderada)"
        AnemiaSeverity.SEVERE -> "Nivel Crítico (Anemia Severa)"
    }

    val minHb = 5.0
    val maxHb = 18.0
    val clamped = hemoglobinLevel.coerceIn(minHb, maxHb)
    val fraction = ((clamped - minHb) / (maxHb - minHb)).toFloat()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ClinicalCardLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Fila superior: Título e indicador de lectura
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bloodtype,
                        contentDescription = null,
                        tint = ClinicalPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "Nivel de Hemoglobina",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClinicalOnSurface
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color(0xFFE5EEFF), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(severityColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = if (severity == AnemiaSeverity.NORMAL) "Lectura Óptima" else "Revisión Sugerida",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ClinicalOnSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Medidor Semicircular Espectral con Aguja Médica (Stitch Design)
            Box(
                modifier = Modifier
                    .size(width = 250.dp, height = 135.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Canvas(modifier = Modifier.size(width = 250.dp, height = 135.dp)) {
                    val w = size.width
                    val h = size.height
                    val strokeW = 14.dp.toPx()
                    val radius = (w - strokeW - 16.dp.toPx()) / 2f
                    val centerX = w / 2f
                    val centerY = h - 6.dp.toPx()

                    // Arco de fondo gris guía
                    drawArc(
                        color = Color(0xFFE2E8F0),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(centerX - radius, centerY - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )

                    // Arco de gradiente espectral continuo (Rojo -> Naranja -> Ámbar -> Esmeralda)
                    val spectrumBrush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFDC2626), // Severa <8
                            Color(0xFFEA580C), // Moderada 8-10.9
                            Color(0xFFF59E0B), // Leve 11-11.9
                            Color(0xFF10B981), // Normal
                            Color(0xFF059669)  // Óptimo >13
                        ),
                        startX = centerX - radius,
                        endX = centerX + radius
                    )

                    drawArc(
                        brush = spectrumBrush,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(centerX - radius, centerY - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )

                    // Marcas de umbral clínico (11.0 g/dL y 12.0 g/dL)
                    val thresholds = listOf(11.0, 12.0)
                    for (t in thresholds) {
                        val tFrac = ((t - minHb) / (maxHb - minHb)).toFloat()
                        val tAngleRad = Math.PI * (1f - tFrac)
                        val innerR = radius - strokeW / 2f - 1.dp.toPx()
                        val outerR = radius + strokeW / 2f + 1.dp.toPx()
                        val p1 = Offset(
                            (centerX + innerR * cos(tAngleRad)).toFloat(),
                            (centerY - innerR * sin(tAngleRad)).toFloat()
                        )
                        val p2 = Offset(
                            (centerX + outerR * cos(tAngleRad)).toFloat(),
                            (centerY - outerR * sin(tAngleRad)).toFloat()
                        )
                        drawLine(
                            color = Color.White,
                            start = p1,
                            end = p2,
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // Aguja Médica de Alta Precisión apuntando al valor
                    val needleAngleRad = Math.PI * (1f - fraction)
                    val needleLength = radius - 8.dp.toPx()
                    val needleTip = Offset(
                        (centerX + needleLength * cos(needleAngleRad)).toFloat(),
                        (centerY - needleLength * sin(needleAngleRad)).toFloat()
                    )

                    // Sombra sutil de la aguja
                    drawLine(
                        color = Color(0x33000000),
                        start = Offset(centerX, centerY + 2.dp.toPx()),
                        end = Offset(needleTip.x, needleTip.y + 2.dp.toPx()),
                        strokeWidth = 3.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Cuerpo de la aguja
                    drawLine(
                        color = Color(0xFF0F172A),
                        start = Offset(centerX, centerY),
                        end = needleTip,
                        strokeWidth = 3.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Pivote central
                    drawCircle(color = Color(0xFF0F172A), radius = 7.dp.toPx(), center = Offset(centerX, centerY))
                    drawCircle(color = Color.White, radius = 3.dp.toPx(), center = Offset(centerX, centerY))
                }
            }

            // Valor Clínico Central
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = String.format(java.util.Locale.US, "%.1f", hemoglobinLevel),
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ClinicalOnSurface,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = " g/dL",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ClinicalTextSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Text(
                text = "Estimación mediante escaneo de $sampleType",
                fontSize = 12.sp,
                color = ClinicalTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Badge de Severidad Clínica Destacado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(severityBgColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = severityColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = severityBadgeText,
                        color = severityColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Rango de Referencia Clínico OMS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEFF4FF), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = ClinicalPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = "Rango esperado saludable:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = ClinicalTextSecondary
                            )
                        }
                        Text(
                            text = "${whoThreshold} - 16.0 g/dL",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClinicalOnSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (severity == AnemiaSeverity.NORMAL) {
                            "Resultado dentro de parámetros normales. Continúa con hábitos de vida activos y alimentación rica en nutrientes."
                        } else {
                            "Resultado preliminar orientativo. Con ${String.format(java.util.Locale.US, "%.1f", hemoglobinLevel)} g/dL es habitual sentir fatiga, falta de energía o palidez. Suele corregirse favorablemente con orientación médica."
                        },
                        fontSize = 12.sp,
                        color = ClinicalTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
