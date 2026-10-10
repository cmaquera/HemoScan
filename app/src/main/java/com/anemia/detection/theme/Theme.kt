package com.anemia.detection.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ClinicalPrimary,
    onPrimary = Color.White,
    primaryContainer = ClinicalPrimaryDark,
    secondary = ClinicalSecondary,
    tertiary = ClinicalAiCyan,
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC)
)

private val LightColorScheme = lightColorScheme(
    primary = ClinicalPrimary,
    onPrimary = Color.White,
    primaryContainer = ClinicalPrimaryContainer,
    secondary = ClinicalSecondary,
    secondaryContainer = ClinicalSecondaryContainer,
    tertiary = ClinicalAiCyan,
    background = ClinicalBackground,
    surface = ClinicalCardLowest,
    surfaceVariant = ClinicalContainerLow,
    onSurface = ClinicalOnSurface,
    onSurfaceVariant = ClinicalOnSurfaceVariant,
    outline = ClinicalOutlineVariant
)

@Composable
fun AnemiaDetectionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Usar tema clínico uniforme de precisión médica
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
