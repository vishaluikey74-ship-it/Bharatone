package com.example.ui.util

import android.content.Context
import android.content.Intent
import com.example.data.model.JobListing
import com.example.data.model.JobType
import com.example.data.model.Listing
import com.example.data.model.ListingDomain
import com.example.data.model.NewsArticle
import com.example.data.model.SocialPost

object ShareHelper {
    const val APP_PACKAGE_NAME = "com.aistudio.bharatone.app"
    const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=$APP_PACKAGE_NAME"

    fun shareApp(context: Context, isHindi: Boolean = true) {
        val message = if (isHindi) {
            """
            🇮🇳 भारतवन (BharatOne) ऐप डाउनलोड करें!
            
            🏠 अपने शहर व ज़िले में कमरे / फ्लैट / PG किराए पर पाएं।
            🛒 सेकंड-हैंड सामान, बाइक, मोबाइल खरीदें व बेचें।
            💼 सार्वजनिक भर्ती और नौकरियों की ताज़ा जानकारी।
            📰 विश्वसनीय स्थानीय हिंदी समाचार व अपडेट्स।
            
            आज ही Google Play Store से डाउनलोड करें:
            $PLAY_STORE_URL
            """.trimIndent()
        } else {
            """
            🇮🇳 Download BharatOne App!
            
            🏠 Find Rooms, Flats & PG rentals in your local district.
            🛒 Buy & Sell used items, bikes, electronics locally.
            💼 Latest Public & Private Job notifications & vacancy alerts.
            📰 Trusted local breaking news & community updates.
            
            Download now from Google Play Store:
            $PLAY_STORE_URL
            """.trimIndent()
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_TITLE, "BharatOne - Local Rooms, Marketplace & Jobs")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, if (isHindi) "BharatOne शेयर करें" else "Share BharatOne")
        context.startActivity(shareIntent)
    }

    fun shareListing(context: Context, listing: Listing, isHindi: Boolean = true) {
        val domainIcon = if (listing.domain == ListingDomain.ROOM_RENTAL) "🏠" else "🛒"
        val domainText = if (listing.domain == ListingDomain.ROOM_RENTAL) {
            if (isHindi) "कमरा / रूम रेंटल" else "Room / Flat Rental"
        } else {
            if (isHindi) "मार्केटप्लेस सामान" else "Marketplace Item"
        }

        val priceText = "₹${listing.price.toInt()}" + if (listing.domain == ListingDomain.ROOM_RENTAL) " / माह" else ""

        val message = """
        $domainIcon $domainText: ${listing.title}
        💰 कीमत: $priceText
        📍 स्थान: ${listing.area}, ${listing.district}
        👤 संपर्क: ${listing.sellerName}
        
        📸 तस्वीरें देखने, सीधे कॉल व चैट करने के लिए BharatOne ऐप Google Play Store से डाउनलोड करें:
        $PLAY_STORE_URL
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_TITLE, listing.title)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, if (isHindi) "विज्ञापन शेयर करें (Play Store लिंक)" else "Share Listing"))
    }

    fun shareNews(context: Context, news: NewsArticle, isHindi: Boolean = true) {
        val headline = if (isHindi) news.hindiHeadline else news.englishHeadline
        val summary = if (isHindi) news.hindiSummary else news.englishSummary
        val prefix = if (news.isBreaking) "🚨 [ताज़ा बड़ी खबर / BREAKING]" else "📰 [स्थानीय समाचार]"

        val message = """
        $prefix
        $headline
        
        $summary
        
        📍 क्षेत्र: ${news.area}, ${news.district} (${news.state})
        🗞️ स्रोत: ${news.sourceName}
        
        पूरी खबर पढ़ने व अपने शहर के सभी ताज़ा अपडेट्स के लिए BharatOne ऐप Google Play Store से डाउनलोड करें:
        $PLAY_STORE_URL
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_TITLE, headline)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, if (isHindi) "समाचार शेयर करें (Play Store लिंक)" else "Share News"))
    }

    fun shareSocialPost(context: Context, post: SocialPost, isHindi: Boolean = true) {
        val message = """
        📢 BharatOne पर ${post.authorName} (@${post.authorUsername}) का पोस्ट:
        
        "${post.text}"
        
        📍 स्थान: ${post.locationName}
        
        लोकल कम्युनिटी चर्चा में भाग लेने के लिए BharatOne ऐप Google Play Store से डाउनलोड करें:
        $PLAY_STORE_URL
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, if (isHindi) "पोस्ट शेयर करें (Play Store लिंक)" else "Share Post"))
    }

    fun shareJob(context: Context, job: JobListing, isHindi: Boolean = true) {
        val title = if (isHindi && job.hindiPostName.isNotBlank()) job.hindiPostName else job.postName
        val org = job.companyName ?: job.organization
        val typeBadge = if (job.jobType == JobType.GOVERNMENT) "🏛️ [सार्वजनिक भर्ती / Public Recruitment]" else "🏢 [प्राइवेट व स्थानीय नौकरी]"

        val message = """
        $typeBadge
        📌 पद: $title
        🏢 विभाग / कंपनी: $org
        📍 स्थान: ${job.location}, ${job.district}
        👥 कुल रिक्तियां: ${job.vacancy}
        🎓 योग्यता: ${job.qualification}
        💰 वेतन: ${job.salary}
        ⏳ अंतिम तिथि: ${job.lastDate}
        
        आधिकारिक अधिसूचना व ऑनलाइन आवेदन लिंक के लिए BharatOne ऐप Google Play Store से तुरंत डाउनलोड करें:
        $PLAY_STORE_URL
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_TITLE, title)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, if (isHindi) "नौकरी शेयर करें (Play Store लिंक)" else "Share Job"))
    }

    fun shareJob(context: Context, jobTitle: String, company: String, location: String, salary: String, isHindi: Boolean = true) {
        val message = """
        💼 नौकरी का अवसर: $jobTitle
        🏢 कंपनी / नियोक्ता: $company
        📍 स्थान: $location
        💰 वेतन / स्टाइपेंड: $salary
        
        इस नौकरी के लिए आवेदन करने व और रिक्तियां देखने के लिए BharatOne ऐप Google Play Store से डाउनलोड करें:
        $PLAY_STORE_URL
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, if (isHindi) "नौकरी शेयर करें (Play Store लिंक)" else "Share Job"))
    }

    fun openWebUrl(context: Context, url: String) {
        try {
            val uri = android.net.Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Log or gracefully ignore if no browser available
        }
    }
}
