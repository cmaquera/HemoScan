package com.anemia.detection.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.anemia.detection.theme.ClinicalTextSecondary
import com.anemia.detection.theme.SeverityMild
import com.anemia.detection.theme.SeverityNormal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBackClick: () -> Unit,
    onResultClick: (DetectionResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { HistoryRepository(context) }
    var historyList by remember { mutableStateOf(repository.getAllResults()) }
    var showDeleteAllConfirm by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<DetectionResult?>(null) }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Chequeos", fontWeight = FontWeight.Bold, color = ClinicalOnSurface) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = ClinicalOnSurface)
                    }
                },
                actions = {
                    if (historyList.isNotEmpty()) {
                        IconButton(onClick = { showDeleteAllConfirm = true }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Borrar Todo", tint = ClinicalOnSurfaceVariant)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ClinicalCardLowest)
            )
        }
    ) { innerPadding ->
        if (historyList.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(ClinicalBackground),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = ClinicalTextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Sin chequeos registrados",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClinicalOnSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tus resultados de chequeo se guardarán aquí de forma 100% privada.",
                        fontSize = 13.sp,
                        color = ClinicalTextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(ClinicalBackground)
                    .padding(16.dp)
            ) {
                // Cabecera informativa de almacenamiento local
                item {
                    val unsynced = historyList.count { !it.isSynced }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ClinicalContainerLow, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${historyList.size} chequeos en este móvil",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClinicalPrimary
                            )
                            Text(
                                text = if (unsynced > 0) "$unsynced pendientes de nube" else "Sincronizado al 100%",
                                fontSize = 11.sp,
                                color = if (unsynced > 0) Color(0xFFE65100) else SeverityNormal,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(historyList, key = { it.id }) { item ->
                    HistoryItemCard(
                        item = item,
                        onClick = { onResultClick(item) },
                        onDeleteClick = { itemToDelete = item }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        // Diálogo para eliminar UN chequeo específico
        itemToDelete?.let { target ->
            val formattedDate = dateFormat.format(Date(target.timestamp))
            AlertDialog(
                onDismissRequest = { itemToDelete = null },
                title = { Text("¿Eliminar este chequeo?", fontWeight = FontWeight.Bold) },
                text = {
                    Text("Se borrará el registro de ${target.severity.displayName} (${String.format(Locale.US, "%.1f", target.hemoglobinLevel)} g/dL) realizado el $formattedDate.")
                },
                confirmButton = {
                    TextButton(onClick = {
                        repository.deleteResult(target.id)
                        historyList = repository.getAllResults()
                        itemToDelete = null
                    }) {
                        Text("Eliminar", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { itemToDelete = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Diálogo para borrar TODO el historial
        if (showDeleteAllConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteAllConfirm = false },
                title = { Text("¿Eliminar todo el historial?", fontWeight = FontWeight.Bold) },
                text = { Text("Esta acción borrará permanentemente todos los ${historyList.size} registros almacenados en el teléfono.") },
                confirmButton = {
                    TextButton(onClick = {
                        repository.clearHistory()
                        historyList = emptyList()
                        showDeleteAllConfirm = false
                    }) {
                        Text("Borrar Todo", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteAllConfirm = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
private fun HistoryItemCard(
    item: DetectionResult,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val severityColor = when (item.severity) {
        AnemiaSeverity.NORMAL -> SeverityNormal
        AnemiaSeverity.MILD -> SeverityMild
        AnemiaSeverity.MODERATE -> Color(0xFFEA580C)
        AnemiaSeverity.SEVERE -> Color(0xFFDC2626)
    }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    val formattedDate = remember(item.timestamp) { dateFormat.format(Date(item.timestamp)) }

    val humanSampleType = if (item.sampleType.contains("Ojo") || item.sampleType.contains("Conjuntiva")) {
        "Interior del Ojo"
    } else {
        "Uña de la Mano"
    }

    val shortModelLabel = when {
        item.modelUsed.contains("HemaViT", ignoreCase = true) -> "HemaViT"
        item.modelUsed.contains("MobileNet", ignoreCase = true) -> "MobileNetV3"
        else -> "Motor Clínico"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ClinicalCardLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Indicador numérico de Hemoglobina (Izquierda, tamaño fijo y cuadrado)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(severityColor.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format(Locale.US, "%.1f", item.hemoglobinLevel),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = severityColor,
                        maxLines = 1,
                        softWrap = false
                    )
                    Text(
                        text = "g/dL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = severityColor,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 2. Bloque central estructurado en 3 líneas limpias sin desbordes
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Línea 1: Diagnóstico / Severidad en su propia fila completa
                Text(
                    text = item.severity.displayName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClinicalOnSurface,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )

                // Línea 2: Fecha y tipo de muestra
                Text(
                    text = "$formattedDate • $humanSampleType",
                    fontSize = 11.sp,
                    color = ClinicalTextSecondary,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )

                // Línea 3: Fila dedicada de etiquetas compactas (sin saltos de línea)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Badge 1: Modo / Modelo corto
                    Box(
                        modifier = Modifier
                            .background(
                                if (item.isNeuralInference) ClinicalContainerHigh else Color(0xFFFFF3E0),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (item.isNeuralInference) "IA • $shortModelLabel" else "Motor Clínico",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.isNeuralInference) ClinicalPrimary else Color(0xFFE65100),
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    // Badge 2: Cantidad de tomas
                    Box(
                        modifier = Modifier
                            .background(ClinicalContainerLow, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (item.isMultiShot) "${item.shotCount} Fotos" else "1 Foto",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClinicalPrimary,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    // Badge 3: Sincronización (Nube / Local)
                    Box(
                        modifier = Modifier
                            .background(
                                if (item.isSynced) Color(0xFFE8F5E9) else Color(0xFFF1F5F9),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (item.isSynced) "Nube" else "Local",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.isSynced) SeverityNormal else Color(0xFF64748B),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // 3. Acciones derechas perfectamente alineadas al centro
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Eliminar chequeo",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(19.dp)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = ClinicalTextSecondary.copy(alpha = 0.45f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
