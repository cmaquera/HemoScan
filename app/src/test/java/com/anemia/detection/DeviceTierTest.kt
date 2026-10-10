package com.anemia.detection

import com.anemia.detection.models.DeviceInfoProfile
import com.anemia.detection.models.DeviceTier
import com.anemia.detection.models.ModelSelectionMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviceTierTest {

    @Test
    fun testAutomaticModeSelectsMobileNetForLowEndDevice() {
        val lowEndProfile = DeviceInfoProfile(
            totalRamGB = 2.0,
            isLowRamDevice = true,
            cpuCores = 4,
            deviceModel = "Test Budget Phone",
            osVersion = "Android 14 (Go Edition)",
            hardwareTier = DeviceTier.LOW_END,
            selectionMode = ModelSelectionMode.AUTOMATIC
        )

        assertTrue("En gama baja el modo automático debe seleccionar el modelo ligero", lowEndProfile.isRunningLightweight)
        assertEquals("MobileNetV3 (Auto: Gama Baja)", lowEndProfile.activeModelName)
    }

    @Test
    fun testAutomaticModeSelectsHemaViTForMidHighEndDevice() {
        val highEndProfile = DeviceInfoProfile(
            totalRamGB = 8.0,
            isLowRamDevice = false,
            cpuCores = 8,
            deviceModel = "Test Flagship Phone",
            osVersion = "Android 15",
            hardwareTier = DeviceTier.MID_HIGH_END,
            selectionMode = ModelSelectionMode.AUTOMATIC
        )

        assertFalse("En gama alta el modo automático no debe ser ligero (debe ser HemaViT)", highEndProfile.isRunningLightweight)
        assertEquals("HemaViT (Auto: Gama Media/Alta)", highEndProfile.activeModelName)
    }

    @Test
    fun testManualModeOverridesHardwareTier() {
        // Dispositivo de gama baja donde el usuario fuerza HemaViT
        val forcedHemaViT = DeviceInfoProfile(
            totalRamGB = 2.0,
            isLowRamDevice = true,
            cpuCores = 4,
            deviceModel = "Budget Phone",
            osVersion = "Android 14",
            hardwareTier = DeviceTier.LOW_END,
            selectionMode = ModelSelectionMode.FORCE_HEMA_VIT
        )

        assertFalse(forcedHemaViT.isRunningLightweight)
        assertEquals("HemaViT (Alta Precisión)", forcedHemaViT.activeModelName)

        // Dispositivo de gama alta donde el usuario fuerza MobileNetV3 (modo ahorro)
        val forcedMobileNet = DeviceInfoProfile(
            totalRamGB = 12.0,
            isLowRamDevice = false,
            cpuCores = 8,
            deviceModel = "Flagship Phone",
            osVersion = "Android 15",
            hardwareTier = DeviceTier.MID_HIGH_END,
            selectionMode = ModelSelectionMode.FORCE_MOBILENET_V3
        )

        assertTrue(forcedMobileNet.isRunningLightweight)
        assertEquals("MobileNetV3 (Modo Rápido)", forcedMobileNet.activeModelName)
    }

    @Test
    fun testForceCalibratedEngineMode() {
        val forcedCalibrated = DeviceInfoProfile(
            totalRamGB = 8.0,
            isLowRamDevice = false,
            cpuCores = 8,
            deviceModel = "Flagship Phone",
            osVersion = "Android 16",
            hardwareTier = DeviceTier.MID_HIGH_END,
            selectionMode = ModelSelectionMode.FORCE_CALIBRATED_ENGINE
        )

        assertTrue("Debe marcarse como motor calibrado forzado", forcedCalibrated.isForcedCalibratedEngine)
        assertEquals("Motor Clínico Calibrado", forcedCalibrated.activeModelName)
    }
}
