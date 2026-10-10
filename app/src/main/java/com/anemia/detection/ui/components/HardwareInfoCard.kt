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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anemia.detection.models.DeviceInfoProfile
import com.anemia.detection.models.ModelSelectionMode
import com.anemia.detection.theme.ClinicalPrimary

@Composable
fun HardwareInfoCard(
    profile: DeviceInfoProfile,
    onConfigureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCalibrated = profile.isForcedCalibratedEngine
    val isLightweight = profile.isRunningLightweight
    val primaryColor = ClinicalPrimary
    val accentColor = when {
        isCalibrated -> Color(0xFF0D47A1)
        isLightweight -> Color(0xFFE65100)
        else -> primaryColor
    }

    val headerIcon = when {
        isCalibrated -> Icons.Default.MedicalServices
        isLightweight -> Icons.Default.Bolt
        else -> Icons.Default.Psychology
    }

    val motorName = when {
        isCalibrated -> "Motor: Clínico Calibrado"
        isLightweight -> "Motor: MobileNetV3"
        else -> "Motor: HemaViT"
    }

    val motorDesc = when {
        isCalibrated -> "Espectrofotometría tisular biofísica"
        isLightweight -> "Gama baja/media • Inferencia ultra rápida"
        else -> "Máxima precisión • Transformer"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onConfigureClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = headerIcon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = motorName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFECEFF1), RoundedCornerShape(6.dp))
                                .border(0.5.dp, Color.LightGray, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (profile.selectionMode == ModelSelectionMode.AUTOMATIC) "Auto" else "Manual",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                        }
                    }
                    Text(
                        text = motorDesc,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                IconButton(onClick = onConfigureClick) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Configurar",
                        tint = primaryColor
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 0.8.dp,
                color = Color(0xFFECEFF1)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(
                    icon = Icons.Default.Memory,
                    label = "RAM Total",
                    value = "${profile.totalRamGB} GB"
                )
                MetricItem(
                    icon = Icons.Default.Speed,
                    label = "Cálculo",
                    value = when {
                        isCalibrated -> "Espectro Óptico"
                        isLightweight -> "0.22 GFLOPs"
                        else -> "3.28 GFLOPs"
                    }
                )
                MetricItem(
                    icon = Icons.Default.Bolt,
                    label = "Inferencia",
                    value = when {
                        isCalibrated -> "~12 ms"
                        isLightweight -> "~28 ms"
                        else -> "~32 ms"
                    }
                )
                MetricItem(
                    icon = Icons.Default.WifiOff,
                    label = "Conexión",
                    value = "100% Offline"
                )
            }
        }
    }
}

@Composable
private fun MetricItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF78909C),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF263238)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}
