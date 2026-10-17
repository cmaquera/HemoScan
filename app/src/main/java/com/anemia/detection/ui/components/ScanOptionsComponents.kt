package com.anemia.detection.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anemia.detection.theme.ClinicalCardLowest
import com.anemia.detection.theme.ClinicalContainerHigh
import com.anemia.detection.theme.ClinicalContainerLow
import com.anemia.detection.theme.ClinicalOnSurface
import com.anemia.detection.theme.ClinicalOnSurfaceVariant
import com.anemia.detection.theme.ClinicalPrimary
import com.anemia.detection.theme.ClinicalTextSecondary
import com.anemia.detection.theme.SeverityNormal

object SampleTypes {
    const val EYE = "Interior del Ojo"
    const val NAIL = "Uña de la Mano"
}

@Composable
fun ModeOptionItem(
    title: String,
    subtitle: String,
    badge: String?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = ClinicalPrimary

    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) ClinicalContainerLow else ClinicalCardLowest
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) primaryColor else Color(0xFFE2E8F0)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
                .height(115.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (badge != null) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) ClinicalPrimary.copy(alpha = 0.12f) else Color(0xFFE8F5E9),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) primaryColor else Color(0xFF2E7D32)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(1.dp))
                }
            }

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) primaryColor else ClinicalOnSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = ClinicalTextSecondary,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun SampleOptionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = ClinicalPrimary

    Box(
        modifier = modifier
            .background(
                if (isSelected) ClinicalContainerLow else ClinicalCardLowest,
                RoundedCornerShape(14.dp)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) primaryColor else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
            .height(95.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) primaryColor else Color.Gray,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) primaryColor else ClinicalOnSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = ClinicalTextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ProtocolStep(number: String, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(ClinicalContainerHigh, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ClinicalPrimary
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = ClinicalOnSurfaceVariant,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun SingleShotCard(selectedSampleType: String) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ClinicalCardLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(ClinicalContainerHigh, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = ClinicalPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Consejos para una Foto Clara",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClinicalOnSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            ProtocolStep(
                number = "1",
                text = "Buena luz: Ubícate frente a una ventana o en un ambiente bien iluminado sin sombras."
            )
            ProtocolStep(
                number = "2",
                text = if (selectedSampleType == SampleTypes.EYE) {
                    "Baja con suavidad el párpado inferior con la yema del pulgar hasta ver el color rosado interior."
                } else {
                    "Apoya la mano relajada sobre una mesa blanca con la uña libre de esmalte."
                }
            )
            ProtocolStep(
                number = "3",
                text = "Mantén el teléfono a 20-25 cm de distancia y enfoca con nitidez antes de disparar."
            )
        }
    }
}

@Composable
fun MultiShotGuidedCard(
    sampleType: String,
    capturedBitmaps: List<Bitmap>,
    onDeletePhoto: (Int) -> Unit,
    onClearAll: () -> Unit
) {
    val totalShots = 4
    val currentStep = capturedBitmaps.size

    val (currentTitle, currentInstruction) = when {
        sampleType.contains("Ojo") -> when (currentStep) {
            0 -> Pair("Toma 1 de 4: Ojo Derecho", "Baja suavemente el párpado inferior derecho con la yema del dedo hasta ver el color rosado.")
            1 -> Pair("Toma 2 de 4: Ojo Derecho", "Segunda toma del ojo derecho asegurando buena luz y enfoque.")
            2 -> Pair("Toma 3 de 4: Ojo Izquierdo", "Ahora haz lo mismo bajando suavemente el párpado inferior izquierdo.")
            3 -> Pair("Toma 4 de 4: Ojo Izquierdo", "Última toma para completar las 4 fotos de máxima precisión.")
            else -> Pair("¡Fotos completas (4/4)!", "Presiona 'Analizar Chequeo Completo' para obtener tu nivel de hemoglobina.")
        }
        else -> when (currentStep) {
            0 -> Pair("Toma 1 de 4: Mano Derecha", "Coloca la mano derecha sobre fondo blanco y enfoca la uña del pulgar.")
            1 -> Pair("Toma 2 de 4: Mano Derecha", "Segunda foto de la mano derecha asegurando nitidez.")
            2 -> Pair("Toma 3 de 4: Mano Izquierda", "Ahora enfoca la uña del pulgar de la mano izquierda.")
            3 -> Pair("Toma 4 de 4: Mano Izquierda", "Última foto para completar la revisión de ambas manos.")
            else -> Pair("¡Fotos completas (4/4)!", "Presiona 'Analizar Chequeo Completo' para procesar los resultados.")
        }
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ClinicalCardLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(ClinicalContainerHigh, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = null,
                            tint = ClinicalPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Secuencia de 4 Fotos",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClinicalOnSurface
                    )
                }

                if (capturedBitmaps.isNotEmpty()) {
                    TextButton(onClick = onClearAll) {
                        Text("Reiniciar", fontSize = 12.sp, color = Color.Red)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Indicadores de Paso (1, 2, 3, 4)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until totalShots) {
                    val isDone = i < capturedBitmaps.size
                    val isCurrent = i == capturedBitmaps.size
                    val stepLabel = if (i < 2) "Der #${i + 1}" else "Izq #${i - 1}"

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    when {
                                        isDone -> SeverityNormal
                                        isCurrent -> ClinicalPrimary
                                        else -> Color(0xFFE2E8F0)
                                    },
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(
                                    text = "${i + 1}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) Color.White else Color.Gray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = stepLabel,
                            fontSize = 10.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) ClinicalPrimary else Color.Gray
                        )
                    }

                    if (i < totalShots - 1) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .padding(horizontal = 6.dp)
                                .background(if (i < capturedBitmaps.size) SeverityNormal else Color(0xFFE2E8F0))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Instrucción Paso Actual
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ClinicalContainerLow, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = currentTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClinicalPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currentInstruction,
                        fontSize = 11.sp,
                        color = ClinicalOnSurfaceVariant,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Miniaturas de Fotos Tomadas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                capturedBitmaps.forEachIndexed { index, bitmap ->
                    Box(
                        modifier = Modifier
                            .size(width = 72.dp, height = 90.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.5.dp, SeverityNormal, RoundedCornerShape(10.dp))
                    ) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Toma ${index + 1}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(2.dp)
                                .size(22.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                .clickable { onDeletePhoto(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Eliminar",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(vertical = 1.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${index + 1}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Espacios pendientes
                for (j in capturedBitmaps.size until totalShots) {
                    Box(
                        modifier = Modifier
                            .size(width = 72.dp, height = 90.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFFCFD8DC), RoundedCornerShape(10.dp))
                            .background(Color(0xFFFAFAFA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Toma ${j + 1}",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyzingView(status: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FA)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            CircularProgressIndicator(
                color = ClinicalPrimary,
                strokeWidth = 4.dp,
                modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "Procesando en tu Teléfono",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ClinicalOnSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = status,
                fontSize = 14.sp,
                color = ClinicalTextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .background(ClinicalContainerLow, RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = null,
                        tint = ClinicalOnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "100% Offline (Sin subir fotos a internet)",
                        fontSize = 12.sp,
                        color = ClinicalOnSurfaceVariant
                    )
                }
            }
        }
    }
}
