package com.example.data.model

data class AdminStats(
    val totalUsers: Int = 18450,
    val activeUsers: Int = 4210,
    val totalRooms: Int = 1240,
    val totalMarketplace: Int = 3890,
    val totalNews: Int = 890,
    val totalJobs: Int = 248,
    val totalPosts: Int = 6720,
    val totalReports: Int = 14,
    val revenueRupees: Double = 148500.0,
    val pendingNewsCount: Int = 6,
    val level1AutoCount: Int = 432,
    val level2PendingReviewCount: Int = 9,
    val level3ManualReviewCount: Int = 5,
    val approvedSourcesCount: Int = 28
)

data class ApprovedSource(
    val id: String,
    val name: String,
    val domainOrUrl: String,
    val category: String, // "Official Gov", "Recruitment Board", "Licensed Agency", "Local Bureau"
    val isOfficialGov: Boolean = false,
    val isRssApiActive: Boolean = true,
    val trustScorePercent: Int = 98,
    val lastSyncTime: String = "Just now",
    val publicationLevel: PublicationLevel = PublicationLevel.LEVEL_1_AUTO
)

enum class PipelineStatus {
    PASSED, FAILED, WARNING, PENDING
}

data class PipelineStepLog(
    val stepIndex: Int,
    val stepName: String,
    val hindiStepName: String,
    val status: PipelineStatus,
    val details: String
)

data class PipelineExecution(
    val runId: String,
    val inputUrlOrText: String,
    val detectedSource: String,
    val timestamp: Long = System.currentTimeMillis(),
    val steps: List<PipelineStepLog>,
    val finalVerdict: String,
    val assignedPublicationLevel: PublicationLevel,
    val generatedSummaryHindi: String
)

data class AdCampaign(
    val id: String,
    val title: String,
    val sponsorName: String,
    val bannerUrl: String = "",
    val targetUrl: String = "https://bharatone.app",
    val impressions: Int = 12400,
    val clicks: Int = 940,
    val isActive: Boolean = true
)

