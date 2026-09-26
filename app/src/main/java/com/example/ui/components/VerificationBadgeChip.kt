package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NewsVerificationStatus
import com.example.data.model.VerificationBadge
import com.example.ui.theme.*

@Composable
fun VerificationBadgeChip(
    badge: VerificationBadge?,
    modifier: Modifier = Modifier
) {
    if (badge == null) return

    val (label, bg, fg) = when (badge) {
        VerificationBadge.VERIFIED_USER -> Triple("✓ Verified User", BharatBlueContainer, VerifiedBadgeBlue)
        VerificationBadge.VERIFIED_SELLER -> Triple("✓ Verified Seller", IndiaGreenContainer, IndiaGreenDark)
        VerificationBadge.VERIFIED_BUSINESS -> Triple("✓ Verified Business", SaffronContainer, SaffronDark)
        VerificationBadge.VERIFIED_NEWS -> Triple("✓ Official News", IndiaGreenContainer, IndiaGreenDark)
        VerificationBadge.EDITOR_REVIEWED -> Triple("✓ Editor Reviewed", BharatBlueContainer, BharatBlue)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bg,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = fg
            )
        }
    }
}

@Composable
fun NewsStatusBadge(
    status: NewsVerificationStatus,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    val (label, bg, fg) = when (status) {
        NewsVerificationStatus.VERIFIED_NEWS -> Triple(
            if (isHindi) "✓ सत्यापित समाचार" else "✓ Verified News",
            IndiaGreenContainer,
            IndiaGreenDark
        )
        NewsVerificationStatus.EDITOR_REVIEWED -> Triple(
            if (isHindi) "✓ संपादक समीक्षित" else "✓ Editor Reviewed",
            BharatBlueContainer,
            BharatBlue
        )
        NewsVerificationStatus.SOURCE_SUMMARY -> Triple(
            if (isHindi) "ℹ️ स्रोत सारांश" else "ℹ️ Source Summary",
            BharatBlueContainer,
            BharatBlue
        )
        NewsVerificationStatus.USER_SUBMITTED -> Triple(
            if (isHindi) "👤 नागरिक पत्रकार" else "👤 User Submitted",
            SaffronContainer,
            SaffronDark
        )
        NewsVerificationStatus.PENDING_MODERATION -> Triple(
            if (isHindi) "⏳ समीक्षाधीन" else "⏳ Pending Review",
            Color(0xFFFEF3C7),
            Color(0xFFD97706)
        )
        NewsVerificationStatus.REJECTED -> Triple(
            if (isHindi) "❌ अस्वीकृत" else "❌ Rejected",
            BreakingNewsRedContainer,
            BreakingNewsRed
        )
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bg,
        modifier = modifier
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = fg,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}
