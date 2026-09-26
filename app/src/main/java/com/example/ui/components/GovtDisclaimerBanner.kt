package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Mandatory Government Information & Non-Affiliation Disclaimer Banner
 * Complies strictly with Google Play Misleading Claims & Government Impersonation policies.
 */
@Composable
fun GovtDisclaimerBanner(
    isHindi: Boolean,
    onSourcesClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = AmberContainer.copy(alpha = 0.28f)
        ),
        border = BorderStroke(1.2.dp, AmberNotice.copy(alpha = 0.85f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("govt_disclaimer_banner")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header Row: Notice Badge + Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Non-Government Entity",
                        tint = AmberNotice,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (isHindi) "⚠️ गैर-सरकारी स्वतंत्र ऐप (अस्वीकरण)" else "⚠️ Non-Government Independent App",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberNotice
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AmberNotice.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (isHindi) "सार्वजनिक सूचना" else "Public Info",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AmberNotice,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Mandatory Disclaimer Text (Exact user specification)
            val disclaimerText = if (isHindi) {
                "अस्वीकरण: BharatOne एक स्वतंत्र निजी ऐप है और किसी भी सरकारी संस्था से जुड़ा नहीं है। जानकारी केवल सूचना के लिए आधिकारिक सार्वजनिक स्रोतों से ली गई है। आवेदन से पहले आधिकारिक वेबसाइट पर जानकारी अवश्य जांचें।"
            } else {
                "Disclaimer: BharatOne is an independent private app and is NOT affiliated with or representing any government entity. Information is taken from official public sources for informational purposes only. Always verify on the official website before applying."
            }

            Text(
                text = disclaimerText,
                fontSize = 11.5.sp,
                lineHeight = 16.5.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (onSourcesClick != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onSourcesClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("disclaimer_view_sources_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = BharatBlue,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isHindi) "🔗 आधिकारिक सरकारी स्रोत (.gov.in) व अस्वीकरण देखें →" else "🔗 Official Govt Sources (.gov.in) & Disclaimer →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BharatBlue
                        )
                    }
                }
            }
        }
    }
}
