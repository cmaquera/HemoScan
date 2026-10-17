package com.anemia.detection.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anemia.detection.engine.DeviceProfiler
import com.anemia.detection.models.ModelSelectionMode
import com.anemia.detection.models.PatientCategory
import com.anemia.detection.models.PatientProfile
import com.anemia.detection.storage.CloudSyncManager
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
import com.anemia.detection.theme.SeverityNormal
import com.anemia.detection.ui.components.HardwareInfoCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val profiler = remember { DeviceProfiler(context) }
    val historyRepo = remember { HistoryRepository(context) }

    var deviceProfile by remember { mutableStateOf(profiler.getDeviceProfile()) }
    var patientProfile by remember { mutableStateOf(profiler.getPatientProfile()) }

    var isSyncing by remember { mutableStateOf(false) }
    var totalRecords by remember { mutableIntStateOf(0) }
    var unsyncedRecords by remember { mutableIntStateOf(0) }
    var isOnline by remember { mutableStateOf(false) }

    fun refreshStorageStats() {
        val all = historyRepo.getAllResults()
        totalRecords = all.size
        unsyncedRecords = all.count { !it.isSynced }
        isOnline = CloudSyncManager.isNetworkAvailable(context)
    }

    LaunchedEffect(Unit) {
        refreshStorageStats()
    }

    val primaryColor = ClinicalPrimary

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración del Diagnóstico", fontWeight = FontWeight.Bold, color = ClinicalOnSurface) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = ClinicalOnSurface)
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
                .padding(16.dp)
        ) {
            // 1. Criterios Clínicos del Diagnóstico (Umbrales de la OMS)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ClinicalCardLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(ClinicalContainerHigh, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Criterios Clínicos del Usuario",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClinicalOnSurface
                            )
                            Text(
                                text = "Ajusta los umbrales de hemoglobina de la OMS",
                                fontSize = 11.sp,
                                color = ClinicalTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Selecciona la categoría biológica de la persona que usa este teléfono para calibrar con exactitud si hay anemia según la Organización Mundial de la Salud:",
                        fontSize = 12.sp,
                        color = ClinicalOnSurfaceVariant,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    PatientCategory.entries.forEach { cat ->
                        val isSelected = patientProfile.category == cat
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val updated = patientProfile.copy(category = cat)
                                    profiler.savePatientProfile(updated)
                                    patientProfile = updated
                                }
                                .padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    val updated = patientProfile.copy(category = cat)
                                    profiler.savePatientProfile(updated)
                                    patientProfile = updated
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = primaryColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = cat.displayName,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = ClinicalOnSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Box(
                                modifier = Modifier
                                    .background(ClinicalContainerLow, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "OMS < ${cat.whoThreshold} g/dL",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Almacenamiento Local y Nube
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ClinicalCardLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(ClinicalContainerHigh, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Storage, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Almacenamiento y Nube",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ClinicalOnSurface
                                )
                                Text(
                                    text = "Offline primero • Respaldo opcional",
                                    fontSize = 11.sp,
                                    color = ClinicalTextSecondary
                                )
                            }
                        }

                        // Badge de red
                        Box(
                            modifier = Modifier
                                .background(if (isOnline) Color(0xFFE8F5E9) else Color(0xFFFFF3E0), RoundedCornerShape(20.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                                    contentDescription = null,
                                    tint = if (isOnline) SeverityNormal else Color(0xFFE65100),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isOnline) "En línea" else "Sin Internet",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOnline) SeverityNormal else Color(0xFFE65100)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Todos tus chequeos se guardan de forma 100% privada en la memoria interna de tu teléfono. Puedes sincronizarlos a la nube cuando tengas una conexión estable.",
                        fontSize = 12.sp,
                        color = ClinicalOnSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(ClinicalContainerLow, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("Guardados en el móvil", fontSize = 10.sp, color = ClinicalTextSecondary)
                                Text("$totalRecords chequeos", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ClinicalOnSurface)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (unsyncedRecords > 0) Color(0xFFFFF8E1) else Color(0xFFE8F5E9),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("Pendientes de nube", fontSize = 10.sp, color = ClinicalTextSecondary)
                                Text(
                                    if (unsyncedRecords > 0) "$unsyncedRecords pendientes" else "Al día (0)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (unsyncedRecords > 0) Color(0xFFE65100) else SeverityNormal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (isSyncing) return@Button
                            isSyncing = true
                            coroutineScope.launch {
                                CloudSyncManager.syncPendingRecords(context) { success, count, msg ->
                                    isSyncing = false
                                    refreshStorageStats()
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        enabled = !isSyncing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ClinicalPrimary)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sincronizando...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (unsyncedRecords > 0) "Sincronizar $unsyncedRecords registros ahora" else "Sincronizar con la Nube",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Hardware Detectado y Motor de IA (Movido desde Inicio a Configuración)
            HardwareInfoCard(
                profile = deviceProfile,
                onConfigureClick = {
                    val modes = ModelSelectionMode.entries
                    val nextMode = modes[(modes.indexOf(deviceProfile.selectionMode) + 1) % modes.size]
                    profiler.setModelSelectionMode(nextMode)
                    deviceProfile = profiler.getDeviceProfile()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ClinicalCardLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(ClinicalContainerHigh, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Memory, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Diagnóstico de Hardware y Motor IA",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClinicalOnSurface
                            )
                            Text(
                                text = "Capacidades de cómputo local",
                                fontSize = 11.sp,
                                color = ClinicalTextSecondary
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFECEFF1))

                    SettingRow("Dispositivo", deviceProfile.deviceModel)
                    SettingRow("Sistema Operativo", deviceProfile.osVersion)
                    SettingRow("Memoria RAM", "${deviceProfile.totalRamGB} GB")
                    SettingRow("Procesador", "${deviceProfile.cpuCores} núcleos")
                    SettingRow("Gama Evaluada", deviceProfile.hardwareTier.displayName)

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ClinicalContainerLow, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(SeverityNormal, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Motor Activo",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ClinicalPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = deviceProfile.activeModelName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClinicalOnSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (deviceProfile.isForcedCalibratedEngine)
                                    "Motor espectrofotométrico clínico de calibración directa en el dispositivo."
                                else
                                    "Red neuronal convolucional con aceleración local (sin envío de fotos a internet).",
                                fontSize = 11.sp,
                                color = ClinicalTextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Selector de Modo de Red Neuronal
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ClinicalCardLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(ClinicalContainerHigh, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Ajuste del Modelo de IA",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClinicalOnSurface
                            )
                            Text(
                                text = "Modo adaptativo o manual",
                                fontSize = 11.sp,
                                color = ClinicalTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    ModelSelectionMode.entries.forEach { mode ->
                        val isSelected = deviceProfile.selectionMode == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    profiler.setModelSelectionMode(mode)
                                    deviceProfile = profiler.getDeviceProfile()
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    profiler.setModelSelectionMode(mode)
                                    deviceProfile = profiler.getDeviceProfile()
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = primaryColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mode.title,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = ClinicalOnSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = mode.description,
                                    fontSize = 11.sp,
                                    color = ClinicalTextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Referencias Científicas
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ClinicalCardLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(ClinicalContainerHigh, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Referencias Científicas",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClinicalOnSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "1. HemaViT: transformer-based deep learning for automated non-invasive anemia detection using conjunctival imaging (Scientific Reports, 2026).\n\n" +
                                "2. Smartphone app for non-invasive detection of anemia using patient-sourced photos (Nature Communications, 2018).\n\n" +
                                "3. Noninvasive Anemia Detection from Retinal Images Using Deep Learning (TVST, 2025).",
                        fontSize = 11.sp,
                        color = ClinicalTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SettingRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = ClinicalTextSecondary)
        Text(
            value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = ClinicalOnSurface
        )
    }
}
