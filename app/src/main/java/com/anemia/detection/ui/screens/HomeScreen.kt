package com.anemia.detection.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anemia.detection.models.AnemiaSeverity
import com.anemia.detection.models.DetectionResult
import com.anemia.detection.storage.HistoryRepository
import com.anemia.detection.theme.ClinicalBackground
import com.anemia.detection.theme.ClinicalCardLowest
import com.anemia.detection.theme.ClinicalContainer
import com.anemia.detection.theme.ClinicalContainerHigh
import com.anemia.detection.theme.ClinicalContainerLow
import com.anemia.detection.theme.ClinicalOnSurface
import com.anemia.detection.theme.ClinicalOnSurfaceVariant
import com.anemia.detection.theme.ClinicalPrimary
import com.anemia.detection.theme.ClinicalPrimaryDark
import com.anemia.detection.theme.ClinicalTextSecondary
import com.anemia.detection.theme.SeverityMild
import com.anemia.detection.theme.SeverityNormal
import com.anemia.detection.theme.SeverityNormalContainer
import com.anemia.detection.ui.components.NutritionGuideDialog
import com.anemia.detection.ui.components.VisualCaptureGuideDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStartScan: () -> Unit,
    onViewHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onViewResult: (DetectionResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val historyRepo = remember { HistoryRepository(context) }

    var lastResult by remember { mutableStateOf<DetectionResult?>(null) }
    var showNutritionDialog by remember { mutableStateOf(false) }
    var showVisualGuideDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        lastResult = historyRepo.getLastResult()
    }

    fun shareSummary() {
        val res = lastResult
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            val message = if (res != null) {
                "📊 *Reporte de Chequeo HemoScan AI*\n" +
                        "Nivel de Hemoglobina: ${String.format(Locale.US, "%.1f", res.hemoglobinLevel)} g/dL\n" +
                        "Resultado: ${res.severity.displayName}\n" +
                        "Fecha: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(res.timestamp))}\n\n" +
                        "_Chequeo preliminar no invasivo. Consulta con un profesional de la salud._"
            } else {
                "💡 Monitorea tus niveles de hemoglobina con HemoScan AI de forma rápida, no invasiva y 100% privada desde tu móvil."
            }
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Compartir resumen de chequeo")
        context.startActivity(shareIntent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(ClinicalContainerHigh, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = ClinicalPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "HemoScan AI",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = ClinicalOnSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(ClinicalContainerHigh, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        "Inicio",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ClinicalPrimary
                                    )
                                }
                            }
                            Text(
                                "Salud en el hogar • 100% Privado",
                                fontSize = 11.sp,
                                color = ClinicalTextSecondary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onViewHistory) {
                        Icon(Icons.Default.History, contentDescription = "Historial de Chequeos", tint = ClinicalOnSurfaceVariant)
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Configuración del Chequeo", tint = ClinicalOnSurfaceVariant)
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 1. Encabezado Claro (Centrado en el usuario del teléfono)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(ClinicalContainerLow, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(SeverityNormal, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Chequeo Personal • 100% Privado",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ClinicalOnSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onOpenSettings() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = ClinicalPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Ajustes de Perfil",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClinicalPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Tu Chequeo de Hemoglobina",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ClinicalOnSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Monitorea tu nivel de hemoglobina de forma rápida, no invasiva y sin dolor desde la cámara.",
                fontSize = 13.sp,
                color = ClinicalOnSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Tarjeta Destacada de Acción Rápida (Hero Clínico)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(ClinicalPrimary, ClinicalPrimaryDark)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    "CHEQUEO EN CASA",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Test Preliminar de Anemia",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Comprueba tus niveles de hemoglobina en pocos segundos analizando la luz reflejada en tus tejidos.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onStartScan,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = ClinicalPrimary
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Iniciar Chequeo Ahora",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                                    .padding(vertical = 8.dp, horizontal = 10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.SentimentSatisfied,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text("Sin dolor", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                                        Text("Sin pinchazos", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                                    .padding(vertical = 8.dp, horizontal = 10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text("Resultado", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                                        Text("En 1 minuto", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Tarjeta de Último Chequeo Registrado
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        lastResult?.let { onViewResult(it) } ?: onViewHistory()
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ClinicalCardLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val res = lastResult
                    val hbValue = res?.hemoglobinLevel ?: 12.8
                    val severity = res?.severity ?: AnemiaSeverity.NORMAL
                    val dateStr = if (res != null) {
                        SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(res.timestamp))
                    } else {
                        "Sin chequeo previo aún"
                    }

                    val severityColor = when (severity) {
                        AnemiaSeverity.NORMAL -> SeverityNormal
                        AnemiaSeverity.MILD -> SeverityMild
                        AnemiaSeverity.MODERATE -> Color(0xFFEA580C)
                        AnemiaSeverity.SEVERE -> Color(0xFFDC2626)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(ClinicalContainerHigh, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = ClinicalPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Tu Último Chequeo",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ClinicalOnSurface
                                )
                                Text(
                                    text = dateStr,
                                    fontSize = 11.sp,
                                    color = ClinicalTextSecondary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(SeverityNormalContainer, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(severityColor, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = severity.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = severityColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ClinicalContainerLow, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = "Hemoglobina estimada:",
                                    fontSize = 13.sp,
                                    color = ClinicalOnSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = String.format(Locale.US, "%.1f", hbValue),
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = severityColor
                                    )
                                    Text(
                                        text = " g/dL",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ClinicalTextSecondary,
                                        modifier = Modifier.padding(bottom = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .background(Color(0xFFE2E8F0), RoundedCornerShape(4.dp))
                            ) {
                                Row(modifier = Modifier.fillMaxSize()) {
                                    Box(
                                        modifier = Modifier
                                            .weight(0.25f)
                                            .background(Color(0xFFDC2626), RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(0.25f)
                                            .background(Color(0xFFD97706))
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(0.50f)
                                            .background(Color(0xFF059669), RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Bajo (<11)", fontSize = 10.sp, color = ClinicalTextSecondary)
                                Text("Leve (11-12)", fontSize = 10.sp, color = ClinicalTextSecondary)
                                Text("Óptimo (≥12)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SeverityNormal)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SeverityNormal,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Evaluación preliminar orientativa para bienestar y vitalidad.",
                            fontSize = 11.sp,
                            color = ClinicalOnSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                lastResult?.let { onViewResult(it) } ?: onViewHistory()
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ver detalles completos del análisis",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClinicalPrimary
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = ClinicalPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onViewHistory() },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ver todo el historial de chequeos",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ClinicalTextSecondary
                        )
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = ClinicalTextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Guía Fotográfica Paso a Paso
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cómo tomar una foto clara",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClinicalOnSurface
                )
                Text(
                    text = "Ver Guía Completa",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ClinicalPrimary,
                    modifier = Modifier.clickable { showVisualGuideDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StitchPhotoTipCard(
                    icon = Icons.Default.Visibility,
                    title = "Interior del ojo con suavidad",
                    desc = "Baja con cariño el párpado inferior con la yema del pulgar hasta ver el color rosado natural sin apretar.",
                    badge = "Cómodo y rápido",
                    onClick = { showVisualGuideDialog = true }
                )
                StitchPhotoTipCard(
                    icon = Icons.Default.WbSunny,
                    title = "Buena luz natural",
                    desc = "Ubícate cerca de una ventana iluminada para que la cámara capte el tono real de la piel y ojos con nitidez.",
                    badge = "Luz clara sin reflejos",
                    onClick = { showVisualGuideDialog = true }
                )
                StitchPhotoTipCard(
                    icon = Icons.Default.HealthAndSafety,
                    title = "Opción: Uña de la mano",
                    desc = "Si no quieres tocarte el ojo, puedes apuntar a la uña del pulgar limpia sin esmalte.",
                    badge = "Sencillo y limpio",
                    onClick = { showVisualGuideDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Accesos Rápidos Funcionales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showNutritionDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ClinicalCardLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(ClinicalContainer, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Restaurant, contentDescription = null, tint = ClinicalPrimary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Alimentos con Hierro", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ClinicalOnSurface)
                            Text("Guía nutricional", fontSize = 10.sp, color = ClinicalTextSecondary)
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { shareSummary() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ClinicalCardLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(ClinicalContainer, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = ClinicalPrimary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Compartir Resumen", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ClinicalOnSurface)
                            Text("Enviar por WhatsApp", fontSize = 10.sp, color = ClinicalTextSecondary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        if (showNutritionDialog) {
            NutritionGuideDialog(onDismiss = { showNutritionDialog = false })
        }

        if (showVisualGuideDialog) {
            VisualCaptureGuideDialog(
                initialSampleType = "Interior del Ojo",
                onDismiss = { showVisualGuideDialog = false }
            )
        }
    }
}

@Composable
private fun StitchPhotoTipCard(
    icon: ImageVector,
    title: String,
    desc: String,
    badge: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ClinicalCardLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .height(150.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(ClinicalContainerHigh, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = ClinicalPrimary, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClinicalOnSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = desc,
                    fontSize = 11.sp,
                    color = ClinicalOnSurfaceVariant,
                    lineHeight = 15.sp,
                    maxLines = 3
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = ClinicalPrimary,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = badge,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ClinicalPrimary
                )
            }
        }
    }
}
