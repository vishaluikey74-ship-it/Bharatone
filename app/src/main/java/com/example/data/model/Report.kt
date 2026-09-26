package com.example.data.model

enum class ReportReason(val displayName: String, val hindiName: String) {
    FAKE_JOB("Fake Job / Recruitment", "फर्जी नौकरी / भर्ती"),
    FRAUD("Fraud / Scam Alert", "धोखाधड़ी / स्कैम"),
    ADVANCE_PAYMENT_SCAM("Advance Payment Scam (Asked for Money)", "पैसे/रजिस्ट्रेशन शुल्क की अनुचित मांग"),
    FAKE_COMPANY("Fake Company / Impersonation", "फर्जी कंपनी / नकली पहचान"),
    WRONG_INFO("Wrong Information / Fabricated Claim", "गलत / मनगढ़ंत जानकारी"),
    SPAM("Spam / Commercial Ads", "स्पैम / अनचाहा विज्ञापन"),
    FAKE_ITEM("Fake Item / Property", "फर्जी सामान / कमरा"),
    OFFENSIVE("Offensive / Hate Content", "आपत्तिजनक सामग्री"),
    ILLEGAL("Illegal Content", "अवैध सामग्री"),
    COPYRIGHT("Copyright Infringement", "कॉपीराइट उल्लंघन"),
    HARASSMENT("Harassment / Misbehavior", "उत्पीड़न / दुर्व्यवहार"),
    OTHER("Other Policy Violation", "अन्य समस्या")
}

data class ReportItem(
    val id: String,
    val targetType: String, // "LISTING", "POST", "NEWS", "JOB", "USER"
    val targetId: String,
    val targetTitle: String,
    val reportedByUserId: String,
    val reason: ReportReason,
    val details: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING" // "PENDING", "RESOLVED", "DISMISSED"
)

