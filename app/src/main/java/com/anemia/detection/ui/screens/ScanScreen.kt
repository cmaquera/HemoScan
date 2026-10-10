package com.anemia.detection.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anemia.detection.engine.AnemiaInferenceEngine
import com.anemia.detection.engine.DeviceProfiler
import com.anemia.detection.engine.ImagePreprocessor
import com.anemia.detection.engine.QualityReport
import com.anemia.detection.models.DetectionResult
import com.anemia.detection.models.PatientProfile
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
import com.anemia.detection.ui.components.AnalyzingView
import com.anemia.detection.ui.components.ModeOptionItem
import com.anemia.detection.ui.components.SampleOptionItem
import com.anemia.detection.ui.components.SingleShotCard
import com.anemia.detection.ui.components.VisualCaptureGuideDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class ScanCaptureMode {
    MULTI_SHOT,  // Modo Guiado (4 Fotos)
    SINGLE_SHOT  // Modo Rápido (1 Foto)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    patientProfile: PatientProfile,
    onBackClick: () -> Unit,
    onResultGenerated: (DetectionResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val preprocessor = remember { ImagePreprocessor() }
    val inferenceEngine = remember { AnemiaInferenceEngine(context) }
    val historyRepo = remember { HistoryRepository(context) }
    val profiler = remember { DeviceProfiler(context) }

    var scanMode by remember { mutableStateOf(ScanCaptureMode.MULTI_SHOT) }
    var selectedSampleType by remember { mutableStateOf("Interior del Ojo") }
    val capturedBitmaps = remember { mutableStateListOf<Bitmap>() }

    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisStatus by remember { mutableStateOf("") }
    var pendingQualityWarning by remember { mutableStateOf<Pair<Bitmap, QualityReport>?>(null) }
    var showVisualGuideDialog by remember { mutableStateOf(false) }

    fun executeSingleInference(bitmap: Bitmap) {
        isAnalyzing = true
        analysisStatus = "Alineando imagen a 224x224..."

        coroutineScope.launch {
            delay(250)
            analysisStatus = "Analizando color y luminosidad de la piel..."
            delay(250)

            val deviceProfile = withContext(Dispatchers.IO) { profiler.getDeviceProfile() }
            analysisStatus = if (deviceProfile.isForcedCalibratedEngine) {
                "Estimando con Motor Clínico en el teléfono..."
            } else {
                "Calculando con IA Local (${deviceProfile.activeModelName})..."
            }

            val result = withContext(Dispatchers.Default) {
                val res = inferenceEngine.analyze(
                    bitmap = bitmap,
                    sampleType = selectedSampleType,
                    patientProfile = patientProfile
                )
                historyRepo.saveResult(res)
                res
            }

            isAnalyzing = false
            onResultGenerated(result)
        }
    }

    fun executeMultiShotInference() {
        if (capturedBitmaps.isEmpty()) return
        isAnalyzing = true
        analysisStatus = "Alineando las ${capturedBitmaps.size} fotos..."

        coroutineScope.launch {
            delay(300)
            analysisStatus = "Analizando variación entre tomas y eliminando reflejos..."
            delay(350)

            val deviceProfile = withContext(Dispatchers.IO) { profiler.getDeviceProfile() }
            analysisStatus = if (deviceProfile.isForcedCalibratedEngine) {
                "Calculando promedio clínico en el teléfono..."
            } else {
                "Procesando las fotos con IA Local (${deviceProfile.activeModelName})..."
            }

            val result = withContext(Dispatchers.Default) {
                val res = inferenceEngine.analyzeMultiShot(
                    bitmaps = capturedBitmaps.toList(),
                    sampleType = selectedSampleType,
                    patientProfile = patientProfile
                )
                historyRepo.saveResult(res)
                res
            }

            isAnalyzing = false
            onResultGenerated(result)
        }
    }

    fun onPhotoReceived(bitmap: Bitmap, skipQualityCheck: Boolean = false) {
        if (!skipQualityCheck) {
            val quality = preprocessor.assessQuality(bitmap)
            if (!quality.isValid) {
                pendingQualityWarning = Pair(bitmap, quality)
                return
            }
        }

        if (scanMode == ScanCaptureMode.SINGLE_SHOT) {
            executeSingleInference(bitmap)
        } else {
            if (capturedBitmaps.size < 4) {
                capturedBitmaps.add(bitmap)
            }
        }
    }

    // Lanzador de Cámara Nativa
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            onPhotoReceived(bitmap)
        }
    }

    // Lanzador de Galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = preprocessor.loadBitmapFromUri(context, uri)
            if (bitmap != null) {
                onPhotoReceived(bitmap)
            }
        }
    }

    var noCameraAlert by remember { mutableStateOf(false) }

    fun launchCameraSafe() {
        try {
            cameraLauncher.launch(null)
        } catch (e: Exception) {
            noCameraAlert = true
        }
    }

    fun loadDemoSamples() {
        try {
            val sampleNames = listOf("eye_right_1.png", "eye_right_2.png", "eye_left_1.png", "eye_left_2.png")
            capturedBitmaps.clear()
            for (name in sampleNames) {
                context.assets.open("samples/$name").use { stream ->
                    val bmp = BitmapFactory.decodeStream(stream)
                    if (bmp != null) {
                        capturedBitmaps.add(bmp)
                    }
                }
            }
        } catch (e: Exception) {
            // Silently ignore if assets not found
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(
                                "Nuevo Chequeo",
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
                                    "100% Privado en tu Teléfono",
                                    fontSize = 11.sp,
                                    color = ClinicalTextSecondary
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = ClinicalOnSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ClinicalCardLowest)
            )
        }
    ) { innerPadding ->
        if (isAnalyzing) {
            AnalyzingView(status = analysisStatus, modifier = Modifier.padding(innerPadding))
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(ClinicalBackground)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Selector de Modo de Chequeo: Guiado (4 Fotos) vs Rápido (1 Foto)
                Text(
                    text = "Modo de Chequeo",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClinicalOnSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    ModeOptionItem(
                        title = "Modo Guiado",
                        subtitle = "4 fotos con guía paso a paso",
                        badge = "Recomendado",
                        isSelected = scanMode == ScanCaptureMode.MULTI_SHOT,
                        onClick = { scanMode = ScanCaptureMode.MULTI_SHOT },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    ModeOptionItem(
                        title = "Modo Rápido",
                        subtitle = "1 foto para chequeo al instante",
                        badge = "1 Foto",
                        isSelected = scanMode == ScanCaptureMode.SINGLE_SHOT,
                        onClick = { scanMode = ScanCaptureMode.SINGLE_SHOT },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Selector de Zona a Fotografiar
                Text(
                    text = "¿Qué zona vas a fotografiar?",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClinicalOnSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    SampleOptionItem(
                        title = "Interior del Ojo",
                        subtitle = "Párpado inferior",
                        icon = Icons.Default.RemoveRedEye,
                        isSelected = selectedSampleType == "Interior del Ojo",
                        onClick = { selectedSampleType = "Interior del Ojo" },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    SampleOptionItem(
                        title = "Uña de la Mano",
                        subtitle = "Uña limpia sin esmalte",
                        icon = Icons.Default.PanTool,
                        isSelected = selectedSampleType == "Uña de la Mano",
                        onClick = { selectedSampleType = "Uña de la Mano" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tarjeta visual interactiva: Guía de encuadre fotográfico
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showVisualGuideDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ClinicalCardLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(ClinicalContainerHigh, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (selectedSampleType == "Interior del Ojo") Icons.Default.Visibility else Icons.Default.PanTool,
                                contentDescription = null,
                                tint = ClinicalPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (selectedSampleType == "Interior del Ojo")
                                    "Cómo enfocar el ojo correctamente"
                                else
                                    "Cómo enfocar la uña correctamente",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClinicalOnSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (selectedSampleType == "Interior del Ojo")
                                    "Baja suavemente la piel debajo de las pestañas hasta ver el color rosado natural."
                                else
                                    "Apoya la mano sobre una mesa clara con la uña libre de esmalte.",
                                fontSize = 11.sp,
                                color = ClinicalTextSecondary,
                                lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ver fotos y ejemplos paso a paso",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ClinicalPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Sección Específica de Captura Guiada
                if (scanMode == ScanCaptureMode.MULTI_SHOT) {
                    MultiShotGuidedCard(
                        sampleType = selectedSampleType,
                        capturedBitmaps = capturedBitmaps,
                        onDeletePhoto = { index ->
                            if (index in capturedBitmaps.indices) {
                                capturedBitmaps.removeAt(index)
                            }
                        },
                        onClearAll = { capturedBitmaps.clear() }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Botones de Acción para Multi-Toma
                    if (capturedBitmaps.size < 4) {
                        Button(
                            onClick = { launchCameraSafe() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ClinicalPrimary)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Tomar Foto (${capturedBitmaps.size + 1} de 4)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ClinicalPrimary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalPrimary)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Añadir de Galería (${capturedBitmaps.size + 1} de 4)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (capturedBitmaps.isEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = { loadDemoSamples() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = ClinicalPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Cargar 4 Fotos de Demostración", fontSize = 13.sp, color = ClinicalPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Botón para procesar ensamble multi-toma
                    if (capturedBitmaps.size >= 2) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { executeMultiShotInference() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (capturedBitmaps.size == 4) Color(0xFF006D77) else Color(0xFF00897B)
                            )
                        ) {
                            Icon(Icons.Default.Science, contentDescription = null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (capturedBitmaps.size == 4) {
                                    "Analizar Chequeo Completo (4 Fotos)"
                                } else {
                                    "Analizar Muestra (${capturedBitmaps.size} de 4 Fotos)"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // Modo Rápido (1 Toma)
                    SingleShotCard(selectedSampleType = selectedSampleType)

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { launchCameraSafe() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ClinicalPrimary)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Tomar Foto con Cámara", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ClinicalPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ClinicalPrimary)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Seleccionar de Galería", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = {
                            try {
                                context.assets.open("samples/eye_right_1.png").use { stream ->
                                    val bmp = BitmapFactory.decodeStream(stream)
                                    if (bmp != null) {
                                        onPhotoReceived(bmp)
                                    }
                                }
                            } catch (e: Exception) {}
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = ClinicalPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cargar Foto de Prueba", fontSize = 13.sp, color = ClinicalPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Diálogo de la Guía Fotográfica Ilustrada
        if (showVisualGuideDialog) {
            VisualCaptureGuideDialog(
                initialSampleType = selectedSampleType,
                onDismiss = { showVisualGuideDialog = false }
            )
        }

        // Diálogo de advertencia de cámara no disponible
        if (noCameraAlert) {
            AlertDialog(
                onDismissRequest = { noCameraAlert = false },
                title = { Text("Cámara no disponible") },
                text = { Text("No se encontró una aplicación de cámara activa en este dispositivo. Puedes seleccionar fotos directamente desde la galería o cargar las muestras de prueba.") },
                confirmButton = {
                    Button(onClick = {
                        noCameraAlert = false
                        galleryLauncher.launch("image/*")
                    }) {
                        Text("Abrir Galería")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { noCameraAlert = false }) {
                        Text("Cerrar")
                    }
                }
            )
        }

        // Diálogo de advertencia de iluminación
        pendingQualityWarning?.let { (bitmap, report) ->
            AlertDialog(
                onDismissRequest = { pendingQualityWarning = null },
                title = { Text("Advertencia de Calidad") },
                text = { Text(report.message) },
                confirmButton = {
                    Button(onClick = {
                        val b = bitmap
                        pendingQualityWarning = null
                        onPhotoReceived(b, skipQualityCheck = true)
                    }) {
                        Text("Continuar de todas formas")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { pendingQualityWarning = null }) {
                        Text("Tomar de nuevo")
                    }
                }
            )
        }
    }
}


@Composable
private fun MultiShotGuidedCard(
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
                    val stepLabel = if (sampleType.contains("Ojo")) {
                        if (i < 2) "Der #${i + 1}" else "Izq #${i - 1}"
                    } else {
                        if (i < 2) "Der #${i + 1}" else "Izq #${i - 1}"
                    }

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

