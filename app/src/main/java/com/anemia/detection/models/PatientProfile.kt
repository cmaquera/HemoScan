package com.anemia.detection.models

enum class PatientCategory(val displayName: String, val whoThreshold: Double) {
    ADULT_MALE("Hombre Adulto (≥15 años)", 13.0),
    ADULT_FEMALE("Mujer Adulta (No gestante)", 12.0),
    PREGNANT_FEMALE("Mujer Gestante", 11.0),
    CHILD("Niño / Adolescente (<15 años)", 11.5)
}

enum class AnemiaSeverity(val displayName: String, val clinicalGuidance: String) {
    NORMAL(
        "Nivel Normal (Sin anemia)",
        "Tus niveles estimados de hemoglobina se encuentran dentro de los parámetros esperados según la OMS. Mantén una dieta rica en hierro y micronutrientes."
    ),
    MILD(
        "Anemia Leve",
        "Se detectan indicios de deficiencia leve de hemoglobina. Se aconseja revisión nutricional y consulta médica preventiva de rutina."
    ),
    MODERATE(
        "Anemia Moderada",
        "Nivel de hemoglobina disminuido compatible con anemia moderada. Se recomienda acudir a un centro de salud para realizar un hemograma completo (CBC)."
    ),
    SEVERE(
        "Anemia Severa",
        "⚠️ ALERTA: Nivel crítico de hemoglobina (< 8.0 g/dL). Es indispensable acudir inmediatamente a un servicio médico de urgencias."
    )
}

data class PatientProfile(
    val category: PatientCategory = PatientCategory.ADULT_FEMALE,
    val age: Int = 30
) {
    val whoThreshold: Double get() = category.whoThreshold

    fun evaluateSeverity(hbLevel: Double): AnemiaSeverity {
        return when {
            hbLevel >= whoThreshold -> AnemiaSeverity.NORMAL
            hbLevel >= 11.0 && whoThreshold > 11.0 -> AnemiaSeverity.MILD
            hbLevel >= 8.0 -> AnemiaSeverity.MODERATE
            else -> AnemiaSeverity.SEVERE
        }
    }
}
