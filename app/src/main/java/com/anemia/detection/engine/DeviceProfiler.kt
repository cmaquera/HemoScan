package com.anemia.detection.engine

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import com.anemia.detection.models.DeviceInfoProfile
import com.anemia.detection.models.DeviceTier
import com.anemia.detection.models.ModelSelectionMode
import com.anemia.detection.models.PatientCategory
import com.anemia.detection.models.PatientProfile

class DeviceProfiler(private val context: Context) {

    private val prefs = context.getSharedPreferences("anemia_detection_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_MODEL_MODE = "key_model_selection_mode"
        private const val KEY_PATIENT_CATEGORY = "key_patient_category"
        private const val KEY_PATIENT_AGE = "key_patient_age"
    }

    fun getDeviceProfile(): DeviceInfoProfile {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalRamGB = memoryInfo.totalMem.toDouble() / (1024.0 * 1024.0 * 1024.0)
        val isLowRam = activityManager.isLowRamDevice
        val cpuCores = Runtime.getRuntime().availableProcessors()

        val deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}".trim()
        val osVersion = "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"

        // Clasificación de hardware:
        // Teléfonos Android con menos de 3.5 GB de RAM, Android Go o <= 4 núcleos se consideran gama baja
        val hardwareTier = if (isLowRam || totalRamGB < 3.5 || cpuCores <= 4) {
            DeviceTier.LOW_END
        } else {
            DeviceTier.MID_HIGH_END
        }

        val savedModeOrdinal = prefs.getInt(KEY_MODEL_MODE, ModelSelectionMode.AUTOMATIC.ordinal)
        val selectionMode = ModelSelectionMode.entries.getOrElse(savedModeOrdinal) { ModelSelectionMode.AUTOMATIC }

        return DeviceInfoProfile(
            totalRamGB = (totalRamGB * 10.0).toInt() / 10.0,
            isLowRamDevice = isLowRam,
            cpuCores = cpuCores,
            deviceModel = deviceModel,
            osVersion = osVersion,
            hardwareTier = hardwareTier,
            selectionMode = selectionMode
        )
    }

    fun setModelSelectionMode(mode: ModelSelectionMode) {
        prefs.edit().putInt(KEY_MODEL_MODE, mode.ordinal).apply()
    }

    fun getPatientProfile(): PatientProfile {
        val catIndex = prefs.getInt(KEY_PATIENT_CATEGORY, PatientCategory.ADULT_FEMALE.ordinal)
        val age = prefs.getInt(KEY_PATIENT_AGE, 30)
        val category = PatientCategory.entries.getOrElse(catIndex) { PatientCategory.ADULT_FEMALE }
        return PatientProfile(category = category, age = age)
    }

    fun savePatientProfile(profile: PatientProfile) {
        prefs.edit()
            .putInt(KEY_PATIENT_CATEGORY, profile.category.ordinal)
            .putInt(KEY_PATIENT_AGE, profile.age)
            .apply()
    }
}
