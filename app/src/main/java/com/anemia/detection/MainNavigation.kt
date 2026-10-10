package com.anemia.detection

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.anemia.detection.engine.DeviceProfiler
import com.anemia.detection.models.DetectionResult
import com.anemia.detection.ui.screens.HistoryScreen
import com.anemia.detection.ui.screens.HomeScreen
import com.anemia.detection.ui.screens.ResultScreen
import com.anemia.detection.ui.screens.ScanScreen
import com.anemia.detection.ui.screens.SettingsScreen

sealed class AppDestination {
    object Home : AppDestination()
    object Scan : AppDestination()
    data class Result(val detectionResult: DetectionResult) : AppDestination()
    object History : AppDestination()
    object Settings : AppDestination()
}

@Composable
fun MainNavigation() {
    val context = LocalContext.current
    val profiler = remember { DeviceProfiler(context) }
    var currentDestination by remember { mutableStateOf<AppDestination>(AppDestination.Home) }

    if (currentDestination !is AppDestination.Home) {
        BackHandler {
            currentDestination = AppDestination.Home
        }
    }

    when (val dest = currentDestination) {
        is AppDestination.Home -> {
            HomeScreen(
                onStartScan = { currentDestination = AppDestination.Scan },
                onViewHistory = { currentDestination = AppDestination.History },
                onOpenSettings = { currentDestination = AppDestination.Settings },
                onViewResult = { res -> currentDestination = AppDestination.Result(res) }
            )
        }
        is AppDestination.Scan -> {
            ScanScreen(
                patientProfile = profiler.getPatientProfile(),
                onBackClick = { currentDestination = AppDestination.Home },
                onResultGenerated = { res -> currentDestination = AppDestination.Result(res) }
            )
        }
        is AppDestination.Result -> {
            ResultScreen(
                result = dest.detectionResult,
                patientProfile = profiler.getPatientProfile(),
                onBackClick = { currentDestination = AppDestination.Home }
            )
        }
        is AppDestination.History -> {
            HistoryScreen(
                onBackClick = { currentDestination = AppDestination.Home },
                onResultClick = { res -> currentDestination = AppDestination.Result(res) }
            )
        }
        is AppDestination.Settings -> {
            SettingsScreen(
                onBackClick = { currentDestination = AppDestination.Home }
            )
        }
    }
}
