package com.anemia.detection.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import android.content.Intent
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anemia.detection.models.DetectionResult
import com.anemia.detection.models.PatientProfile
import com.anemia.detection.theme.ClinicalBackground
import com.anemia.detection.theme.ClinicalCardLowest
import com.anemia.detection.theme.ClinicalContainer
import com.anemia.detection.theme.ClinicalContainerHigh
import com.anemia.detection.theme.ClinicalContainerLow
import com.anemia.detection.theme.ClinicalOnSurface
import com.anemia.detection.theme.ClinicalOnSurfaceVariant
import com.anemia.detection.theme.ClinicalPrimary
import com.anemia.detection.theme.ClinicalTextSecondary
import com.anemia.detection.theme.SeverityNormal
import com.anemia.detection.ui.components.HemoglobinGauge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    result: DetectionResult,
    patientProfile: PatientProfile,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    fun shareResult() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            val dateFormatted = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(result.timestamp))
            val message = "*Reporte de Chequeo HemoScan AI*\n" +
                    "Nivel de Hemoglobina: ${String.format(Locale.US, "%.1f", result.hemoglobinLevel)} g/dL\n" +
                    "Resultado: ${result.severity.displayName}\n" +
                    "Fecha: $dateFormatted\n" +
                    "Protocolo: ${if (result.isMultiShot) "Clínico (4 tomas)" else "Rápido (1 toma)"}\n" +
                    "Confianza: ${(result.confidence * 100).toInt()}%\n\n" +
                    "_Chequeo preliminar no invasivo. Consulta con un profesional de la salud._"
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Compartir resultado de chequeo")
        context.startActivity(shareIntent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(
                                "Resultado del Chequeo",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = ClinicalOnSurface
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(ClinicalPrimary, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Modo Seguro • En tu dispositivo",
                                    fontSize = 11.sp,
                                    color = ClinicalTextSecondary
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = ClinicalOnSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ClinicalCardLowest)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ClinicalBackground)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // 1. Encabezado de Estado Clínico
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFECFDF5), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SeverityNormal,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Chequeo Preliminar Completado",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SeverityNormal
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(ClinicalContainerHigh, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "COMPLETADO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClinicalPrimary,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            val dateStr = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(result.timestamp))
            Text(
                text = "Resultado para: ${patientProfile.category.displayName}, ${patientProfile.age} años • $dateStr",
                fontSize = 12.sp,
                color = ClinicalTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Tarjeta Principal con Medidor Espectral Semicircular (Stitch Design)
            HemoglobinGauge(
                hemoglobinLevel = result.hemoglobinLevel,
                severity = result.severity,
                whoThreshold = patientProfile.whoThreshold,
                sampleType = result.sampleType
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Tarjeta: ¿Qué significa este chequeo preliminar?
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ClinicalCardLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(ClinicalContainer, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = ClinicalPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "¿Qué significa este chequeo?",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClinicalOnSurface
                            )
                            Text(
                                text = "Detalles sobre bienestar diario y energía",
                                fontSize = 11.sp,
                                color = ClinicalTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Ítem Bienestar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ClinicalContainerLow, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = ClinicalPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Bienestar y síntomas habituales",
                                    fontSize = 11.sp,
                                    color = ClinicalTextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (result.isAnemic) {
                                        "Posible fatiga o desgano al final de la jornada, menor energía en actividades o palidez en mucosa palpebral y uñas."
                                    } else {
                                        "Niveles de oxigenación y vitalidad dentro del rango saludable esperado para tu perfil."
                                    },
                                    fontSize = 13.sp,
                                    color = ClinicalOnSurface,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Ítem Calidad
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ClinicalContainerLow, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = SeverityNormal,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Calidad técnica de la captura",
                                    fontSize = 11.sp,
                                    color = ClinicalTextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                val qualityText = if (result.isMultiShot) {
                                    val errText = result.standardError?.let { " (Margen SE ±$it g/dL)" } ?: ""
                                    "Excelente. Protocolo de ${result.shotCount} capturas consolidadas$errText con filtro de aberraciones ópticas activo."
                                } else {
                                    "Óptima. Captura nítida con iluminación uniforme para estimación espectral tisular."
                                }
                                Text(
                                    text = qualityText,
                                    fontSize = 13.sp,
                                    color = ClinicalOnSurface,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Sección de Recomendaciones Clínicas y Pasos Sugeridos
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ClinicalCardLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Pasos sugeridos",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClinicalOnSurface
                    )
                    Text(
                        text = "Acciones prácticas y preventivas",
                        fontSize = 11.sp,
                        color = ClinicalTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Recomendación 1: Nutrición
                    StitchActionItem(
                        icon = Icons.Default.Restaurant,
                        iconTint = SeverityNormal,
                        title = "Mejora nutricional con hierro y vitamina C",
                        desc = "Incorpora lentejas, espinacas, carnes magras o legumbres junto con cítricos para potenciar la absorción natural de hierro."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Recomendación 2: Consulta Médica
                    StitchActionItem(
                        icon = Icons.Default.Psychology,
                        iconTint = ClinicalPrimary,
                        title = "Consulta médica para confirmación con análisis de sangre",
                        desc = "Acude a un profesional de la salud para corroborar el resultado con un hemograma convencional de laboratorio (CBC)."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Recomendación 3: Seguimiento
                    StitchActionItem(
                        icon = Icons.Default.CalendarMonth,
                        iconTint = ClinicalPrimary,
                        title = "Repetir chequeo preliminar en 15 días",
                        desc = "Realiza un nuevo escaneo en dos semanas para monitorizar la evolución y respuesta de los niveles de bienestar."
                    )

                    if (result.recommendations.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Indicaciones específicas para tu resultado",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClinicalOnSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        result.recommendations.forEach { rec ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "•",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ClinicalPrimary,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(
                                    text = rec,
                                    fontSize = 12.sp,
                                    color = ClinicalOnSurfaceVariant,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Aviso Preventivo
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFFBEB), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "HemoScan AI es una herramienta orientativa no invasiva. No reemplaza un diagnóstico médico ni un análisis formal de laboratorio.",
                                fontSize = 11.sp,
                                color = Color(0xFF92400E),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Tarjeta de Transparencia del Motor IA
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ClinicalCardLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = ClinicalPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Detalles de Inferencia en Dispositivo",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClinicalOnSurface
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        thickness = 0.8.dp,
                        color = Color(0xFFECEFF1)
                    )

                    DataRow("Método", result.inferenceMethod)
                    DataRow("Modelo", result.modelUsed)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 6. Botones de Acción al Final (Stitch Design)
            Button(
                onClick = { shareResult() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ClinicalPrimary),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Compartir Resultado",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ClinicalCardLowest,
                    contentColor = ClinicalPrimary
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Realizar un Nuevo Chequeo",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBackClick() }
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = ClinicalTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Volver al Menú Principal",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = ClinicalTextSecondary
                )
            }
        }
    }
}

@Composable
private fun StitchActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    desc: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ClinicalContainerLow, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.White, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClinicalOnSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    fontSize = 11.sp,
                    color = ClinicalOnSurfaceVariant,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun DataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = ClinicalTextSecondary,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = ClinicalOnSurface,
            textAlign = androidx.compose.ui.text.style.TextAlign.End,
            modifier = Modifier.weight(1.8f)
        )
    }
}
