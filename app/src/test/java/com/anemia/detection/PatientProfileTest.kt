package com.anemia.detection

import com.anemia.detection.models.AnemiaSeverity
import com.anemia.detection.models.PatientCategory
import com.anemia.detection.models.PatientProfile
import org.junit.Assert.assertEquals
import org.junit.Test

class PatientProfileTest {

    @Test
    fun testWHOThresholdsMatchClinicalGuidelines() {
        assertEquals(13.0, PatientCategory.ADULT_MALE.whoThreshold, 0.001)
        assertEquals(12.0, PatientCategory.ADULT_FEMALE.whoThreshold, 0.001)
        assertEquals(11.0, PatientCategory.PREGNANT_FEMALE.whoThreshold, 0.001)
        assertEquals(11.5, PatientCategory.CHILD.whoThreshold, 0.001)
    }

    @Test
    fun testSeverityClassificationForAdultFemale() {
        val femaleProfile = PatientProfile(category = PatientCategory.ADULT_FEMALE, age = 28)

        // Normal: >= 12.0
        assertEquals(AnemiaSeverity.NORMAL, femaleProfile.evaluateSeverity(13.5))
        assertEquals(AnemiaSeverity.NORMAL, femaleProfile.evaluateSeverity(12.0))

        // Leve: 11.0 a 11.9
        assertEquals(AnemiaSeverity.MILD, femaleProfile.evaluateSeverity(11.8))
        assertEquals(AnemiaSeverity.MILD, femaleProfile.evaluateSeverity(11.0))

        // Moderada: 8.0 a 10.9
        assertEquals(AnemiaSeverity.MODERATE, femaleProfile.evaluateSeverity(10.5))
        assertEquals(AnemiaSeverity.MODERATE, femaleProfile.evaluateSeverity(8.0))

        // Severa: < 8.0
        assertEquals(AnemiaSeverity.SEVERE, femaleProfile.evaluateSeverity(7.9))
        assertEquals(AnemiaSeverity.SEVERE, femaleProfile.evaluateSeverity(5.2))
    }

    @Test
    fun testSeverityClassificationForAdultMale() {
        val maleProfile = PatientProfile(category = PatientCategory.ADULT_MALE, age = 45)

        // Normal: >= 13.0
        assertEquals(AnemiaSeverity.NORMAL, maleProfile.evaluateSeverity(14.0))

        // Leve: 11.0 a 12.9
        assertEquals(AnemiaSeverity.MILD, maleProfile.evaluateSeverity(12.5))

        // Moderada: 8.0 a 10.9
        assertEquals(AnemiaSeverity.MODERATE, maleProfile.evaluateSeverity(9.2))

        // Severa: < 8.0
        assertEquals(AnemiaSeverity.SEVERE, maleProfile.evaluateSeverity(6.8))
    }

    @Test
    fun testSeverityClassificationForPregnantFemale() {
        val pregnantProfile = PatientProfile(category = PatientCategory.PREGNANT_FEMALE, age = 26)

        // Normal: >= 11.0
        assertEquals(AnemiaSeverity.NORMAL, pregnantProfile.evaluateSeverity(11.5))
        assertEquals(AnemiaSeverity.NORMAL, pregnantProfile.evaluateSeverity(11.0))

        // Leve: 10.0 a 10.9 (OMS gestantes)
        assertEquals(AnemiaSeverity.MILD, pregnantProfile.evaluateSeverity(10.5))
        assertEquals(AnemiaSeverity.MILD, pregnantProfile.evaluateSeverity(10.0))

        // Moderada: 7.0 a 9.9 (OMS gestantes)
        assertEquals(AnemiaSeverity.MODERATE, pregnantProfile.evaluateSeverity(9.5))
        assertEquals(AnemiaSeverity.MODERATE, pregnantProfile.evaluateSeverity(7.0))

        // Severa: < 7.0 (OMS gestantes)
        assertEquals(AnemiaSeverity.SEVERE, pregnantProfile.evaluateSeverity(6.9))
    }

    @Test
    fun testSeverityClassificationForChild() {
        val childProfile = PatientProfile(category = PatientCategory.CHILD, age = 10)

        // Normal: >= 11.5
        assertEquals(AnemiaSeverity.NORMAL, childProfile.evaluateSeverity(11.5))
        // Leve: 11.0 a 11.4
        assertEquals(AnemiaSeverity.MILD, childProfile.evaluateSeverity(11.2))
        // Moderada: 8.0 a 10.9
        assertEquals(AnemiaSeverity.MODERATE, childProfile.evaluateSeverity(9.8))
        // Severa: < 8.0
        assertEquals(AnemiaSeverity.SEVERE, childProfile.evaluateSeverity(7.5))
    }
}
