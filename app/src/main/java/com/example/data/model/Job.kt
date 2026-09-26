package com.example.data.model

enum class JobType(val title: String, val hindiTitle: String) {
    GOVERNMENT("Public Recruitment", "सार्वजनिक भर्ती"),
    PRIVATE("Private & Local Job", "निजी व स्थानीय नौकरी"),
    APPRENTICESHIP("Apprenticeship & Trainee", "अप्रेंटिसशिप व प्रशिक्षण")
}

enum class EmployerVerification(val label: String, val hindiLabel: String, val isVerified: Boolean) {
    VERIFIED_OFFICIAL_GOVT("Public Portal (Non-Govt)", "सार्वजनिक पोर्टल (गैर-सरकारी)", true),
    VERIFIED_EMPLOYER("✓ Verified Employer", "✓ सत्यापित नियोक्ता", true),
    UNVERIFIED_EMPLOYER("ℹ️ Unverified Employer", "ℹ️ अपुष्ट नियोक्ता", false)
}

data class JobListing(
    val id: String,
    val jobType: JobType = JobType.GOVERNMENT,
    val organization: String, // e.g. "Madhya Pradesh Public Service Commission (MPPSC)"
    val postName: String, // e.g. "State Service Exam / Assistant Engineer"
    val hindiPostName: String,
    val vacancy: String, // e.g. "450 Posts" or unconfirmed message
    val qualification: String, // e.g. "Graduate in any stream / B.E."
    val ageLimit: String, // e.g. "21 - 33 Years (Relaxation as per Rules)"
    val salary: String, // e.g. "Pay Matrix Level-10 (₹56,100 - ₹1,77,500)"
    val applicationStartDate: String, // e.g. "05 Sep 2026"
    val lastDate: String, // e.g. "05 Oct 2026"
    val examDate: String = "Tentatively Nov 2026",
    val applicationFee: String = "Gen/OBC: ₹500, SC/ST/Divyang: ₹250",
    val selectionProcess: String = "Prelims CBT + Mains Written Exam + Interview",
    val officialNotificationUrl: String,
    val officialApplyLink: String,
    val officialSource: String, // e.g. "mppsc.mp.gov.in (Official MPPSC Portal)"
    val sourceUrl: String = officialNotificationUrl.ifBlank { officialApplyLink },
    val employerVerification: EmployerVerification = EmployerVerification.UNVERIFIED_EMPLOYER,
    val state: String = "Madhya Pradesh",
    val district: String = "Balaghat",
    val location: String = "Balaghat / MP State-wide",
    val companyName: String? = null,
    val contactPhone: String? = null,
    val contactEmail: String? = null,
    val timing: String = "Full-Time (10 AM - 7 PM)",
    val genderPreference: String = "Any", // "Any", "Male", "Female"
    val jobCategoryTag: String = "General",
    val imageUrl: String = "",
    val publicationLevel: PublicationLevel = PublicationLevel.LEVEL_1_AUTO,
    val publishedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isUnconfirmedMissingInfo: Boolean = false,
    val unconfirmedDisclaimer: String = "इस जानकारी की आधिकारिक पुष्टि उपलब्ध नहीं है। कृपया आधिकारिक नोटिफिकेशन देखें।",
    val description: String = "",
    val hindiDescription: String = "",
    val isReported: Boolean = false
)

/**
 * Ensures government jobs/exams/schemes strictly have a verified .gov.in or .nic.in official notification URL.
 * In compliance with Google Play Misleading Claims policy.
 */
fun JobListing.hasValidOfficialGovUrl(): Boolean {
    if (jobType != JobType.GOVERNMENT) return true
    val candidates = listOf(sourceUrl, officialNotificationUrl, officialApplyLink)
    return candidates.any { url ->
        url.isNotBlank() && (url.contains(".gov.in", ignoreCase = true) || url.contains(".nic.in", ignoreCase = true))
    }
}

fun JobListing.getEffectiveOfficialUrl(): String {
    val candidates = listOf(sourceUrl, officialNotificationUrl, officialApplyLink)
    return candidates.firstOrNull { url ->
        url.isNotBlank() && (url.contains(".gov.in", ignoreCase = true) || url.contains(".nic.in", ignoreCase = true))
    } ?: sourceUrl.ifBlank { officialNotificationUrl.ifBlank { officialApplyLink } }
}
