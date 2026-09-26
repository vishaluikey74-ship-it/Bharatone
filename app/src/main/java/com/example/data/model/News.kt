package com.example.data.model

enum class NewsCategory(val displayName: String, val hindiName: String, val emoji: String) {
    BREAKING("Breaking News", "ब्रेकिंग न्यूज़", "🔴"),
    LOCAL("Local News", "स्थानीय समाचार", "📍"),
    NATIONAL("National News", "देश / राष्ट्रीय", "🇮🇳"),
    GOVT_JOBS("Govt Recruitment", "सरकारी भर्ती व नौकरियां", "🏛️"),
    JOBS("Local & Private Jobs", "निजी रोजगार व काम", "💼"),
    GOVT_SCHEMES("Government Schemes", "सरकारी योजनाएं", "📜"),
    AGRICULTURE("Agriculture & Krishi", "कृषि व किसान", "🌾"),
    POLITICS("Politics", "राजनीति", "🏛️"),
    CRIME("Crime & Police", "अपराध व कानून", "👮"),
    EDUCATION("Education", "शिक्षा व करियर", "🎓"),
    BUSINESS("Business & Market", "व्यापार व अर्थव्यवस्था", "💰"),
    SPORTS("Sports & Cricket", "खेल समाचार", "🏏"),
    WEATHER("Weather & Monsoon", "मौसम अलर्ट", "🌦️"),
    HEALTH("Health & Lifestyle", "स्वास्थ्य व वेलनेस", "🏥"),
    TECHNOLOGY("Tech & Mobile", "तकनीक व ऑटो", "📱"),
    VIRAL("Viral & Community", "वायरल व समुदाय", "🔥")
}

enum class NewsVerificationStatus(val label: String, val hindiLabel: String, val badgeEmoji: String) {
    VERIFIED_NEWS("✓ Verified News", "✓ सत्यापित समाचार", "✓"),
    EDITOR_REVIEWED("✓ Editor Reviewed", "✓ संपादक द्वारा समीक्षित", "✓"),
    SOURCE_SUMMARY("ℹ️ Source-based Summary", "ℹ️ स्रोत-आधारित सारांश", "ℹ️"),
    USER_SUBMITTED("👤 User Submitted", "👤 नागरिक रिपोर्टर", "👤"),
    PENDING_MODERATION("⏳ Pending Review", "⏳ समीक्षाधीन", "⏳"),
    REJECTED("❌ Rejected", "❌ अस्वीकृत", "❌")
}

enum class PublicationLevel(val levelName: String, val hindiTitle: String, val description: String) {
    LEVEL_1_AUTO("Level 1 — Auto Publish", "लेवल 1 — स्वतः प्रकाशित", "Approved official government feeds & authorized news agencies"),
    LEVEL_2_AI_ADMIN("Level 2 — AI Processed + Admin Review", "लेवल 2 — AI प्रोसेस + एडमिन समीक्षा", "Important news, government recruitment & sensitive regional updates"),
    LEVEL_3_MANUAL_REVIEW("Level 3 — Manual Review", "लेवल 3 — अनिवार्य मानव समीक्षा", "User submissions, allegations, crime reports, political claims & sensitive reports")
}

data class SourceAttribution(
    val sourceName: String,
    val sourceUrl: String,
    val isOfficialGovSource: Boolean = false,
    val isApprovedLicenseFeed: Boolean = true,
    val publicationDateTime: String = "01 Sep 2026, 09:30 AM",
    val updatedDateTime: String = "01 Sep 2026, 10:15 AM",
    val originalAuthorOrDept: String = "Press Trust of India / PIB",
    val copyrightNotice: String = "Attributed source summary compliant with BharatOne Content & Copyright Policy. Full text not reproduced without license."
)

data class NewsArticle(
    val id: String,
    val hindiHeadline: String,
    val englishHeadline: String,
    val hindiSummary: String,
    val englishSummary: String,
    val fullContentHindi: String,
    val fullContentEnglish: String,
    val sourceName: String = "Press Information Bureau (PIB)",
    val sourceUrl: String = "https://pib.gov.in",
    val sourceAttribution: SourceAttribution = SourceAttribution(sourceName, sourceUrl),
    val publishedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val state: String = "Madhya Pradesh",
    val district: String = "Balaghat",
    val city: String = "Balaghat City",
    val area: String = "Paraswada",
    val category: NewsCategory = NewsCategory.LOCAL,
    val verificationStatus: NewsVerificationStatus = NewsVerificationStatus.VERIFIED_NEWS,
    val publicationLevel: PublicationLevel = PublicationLevel.LEVEL_1_AUTO,
    val isBreaking: Boolean = false,
    val isUnconfirmedInfo: Boolean = false,
    val unconfirmedNote: String? = null,
    val imageUrl: String = "",
    val authorName: String = "PIB / Bharat News Bureau",
    val authorId: String? = null,
    val viewsCount: Int = 1240,
    val likesCount: Int = 89,
    val sharesCount: Int = 34,
    val isFactChecked: Boolean = true
)

fun NewsArticle.hasValidOfficialGovUrl(): Boolean {
    if (category != NewsCategory.GOVT_SCHEMES && category != NewsCategory.GOVT_JOBS) return true
    return sourceUrl.isNotBlank() && (sourceUrl.contains(".gov.in", ignoreCase = true) || sourceUrl.contains(".nic.in", ignoreCase = true))
}

fun NewsArticle.getEffectiveOfficialUrl(): String {
    return if (sourceUrl.isNotBlank()) sourceUrl else "https://pib.gov.in"
}

