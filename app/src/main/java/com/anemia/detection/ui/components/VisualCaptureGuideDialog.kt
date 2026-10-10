package com.anemia.detection.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.anemia.detection.theme.ClinicalCardLowest
import com.anemia.detection.theme.ClinicalContainerHigh
import com.anemia.detection.theme.ClinicalContainerLow
import com.anemia.detection.theme.ClinicalOnSurface
import com.anemia.detection.theme.ClinicalOnSurfaceVariant
import com.anemia.detection.theme.ClinicalPrimary
import com.anemia.detection.theme.ClinicalTextSecondary
import com.anemia.detection.theme.SeverityNormal

@Composable
fun VisualCaptureGuideDialog(
    initialSampleType: String = "Interior del Ojo",
    onDismiss: () -> Unit
) {
    var selectedTab by remember {
        mutableStateOf(if (initialSampleType.contains("Uña") || initialSampleType.contains("Ungueal")) 1 else 0)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ClinicalCardLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Barra superior
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Guía Fotográfica",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ClinicalOnSurface
                        )
                        Text(
                            text = "Cómo enfocar sin conocimientos médicos",
                            fontSize = 11.sp,
                            color = ClinicalTextSecondary
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = ClinicalTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Selector de pestañas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ClinicalContainerLow, RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (selectedTab == 0) ClinicalPrimary else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedTab = 0 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Visibility,
                                contentDescription = null,
                                tint = if (selectedTab == 0) Color.White else ClinicalOnSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "En el Ojo",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 0) Color.White else ClinicalOnSurfaceVariant
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (selectedTab == 1) ClinicalPrimary else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedTab = 1 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.PanTool,
                                contentDescription = null,
                                tint = if (selectedTab == 1) Color.White else ClinicalOnSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "En la Uña",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 1) Color.White else ClinicalOnSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 0) {
                    // Guía del Ojo
                    EyeGuideContent()
                } else {
                    // Guía de la Uña
                    NailGuideContent()
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ClinicalPrimary)
                ) {
                    Text("¡Listo, vamos a fotografiar!", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun EyeGuideContent() {
    Column {
        // Tarjeta ilustrativa en Compose
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE5EEFF), RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = ClinicalPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Zona a enfocar: Párpado Inferior",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClinicalPrimary
                )
                Text(
                    text = "No mires al lente ni enfoques la parte negra del ojo. Solo necesitamos la piel rosada que está debajo de las pestañas.",
                    fontSize = 11.sp,
                    color = ClinicalOnSurfaceVariant,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "3 Pasos sencillos:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = ClinicalOnSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        InstructionStep(1, "Coloca la yema de tu pulgar sobre el pómulo, justo debajo de las pestañas.")
        InstructionStep(2, "Baja suavemente la piel hacia abajo unos milímetros hasta ver el tejido rosado interior.")
        InstructionStep(3, "Sostén el teléfono a unos 20-25 cm, asegúrate de que esté enfocado y toma la foto.")

        Spacer(modifier = Modifier.height(14.dp))

        // Comparativa Do's & Don'ts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFECFDF5), RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SeverityNormal, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Correcto", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SeverityNormal)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Tejido rosado expuesto, enfoque claro y luz natural de día.", fontSize = 10.sp, color = ClinicalOnSurfaceVariant, lineHeight = 13.sp)
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFFEF2F2), RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Evitar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Fotos borrosas, cerrar el ojo o tapar la cámara con los dedos.", fontSize = 10.sp, color = ClinicalOnSurfaceVariant, lineHeight = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun NailGuideContent() {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE5EEFF), RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PanTool,
                        contentDescription = null,
                        tint = ClinicalPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Zona a enfocar: Uña Limpia",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClinicalPrimary
                )
                Text(
                    text = "Apoya la mano sobre una mesa blanca o papel. Enfoca el centro de la uña del dedo pulgar o índice.",
                    fontSize = 11.sp,
                    color = ClinicalOnSurfaceVariant,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "3 Pasos sencillos:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = ClinicalOnSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        InstructionStep(1, "Coloca la mano relajada y horizontal sobre un fondo claro.")
        InstructionStep(2, "Asegúrate de que la uña esté limpia y totalmente sin esmalte ni brillo.")
        InstructionStep(3, "Acerca la cámara a 15-20 cm sin hacer sombra con el teléfono.")

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFECFDF5), RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SeverityNormal, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Correcto", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SeverityNormal)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Uña al natural, bien iluminada sin sombras directas.", fontSize = 10.sp, color = ClinicalOnSurfaceVariant, lineHeight = 13.sp)
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFFEF2F2), RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Evitar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Esmalte de cualquier color, uñas sucias o poca luz.", fontSize = 10.sp, color = ClinicalOnSurfaceVariant, lineHeight = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun InstructionStep(number: Int, text: String) {
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
                text = "$number",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ClinicalPrimary
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            color = ClinicalOnSurfaceVariant,
            lineHeight = 15.sp
        )
    }
}
