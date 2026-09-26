package com.example.ui.components

object BharatStrings {
    fun t(key: String, lang: String): String {
        val isHindi = lang == "hi"
        return when (key) {
            "app_title" -> if (isHindi) "भारतवन (BharatOne)" else "BharatOne"
            "home" -> if (isHindi) "होम" else "Home"
            "rooms" -> if (isHindi) "कमरे / PG" else "Rooms & PG"
            "market" -> if (isHindi) "मार्केट" else "Market"
            "jobs" -> if (isHindi) "भर्ती / जॉब्स" else "Govt Jobs"
            "post" -> if (isHindi) "पोस्ट +" else "Post +"
            "news" -> if (isHindi) "समाचार" else "News"
            "chat" -> if (isHindi) "चैट" else "Chat"
            "profile" -> if (isHindi) "प्रोफाइल" else "Profile"
            "search_hint" -> if (isHindi) "रूम, कार, बाइक, मोबाइल, समाचार खोजें..." else "Search Rooms, Products, Cars, Bikes, News..."
            "breaking_news" -> if (isHindi) "🔥 ब्रेकिंग न्यूज़" else "🔥 Breaking News"
            "rooms_near_you" -> if (isHindi) "🏠 आपके पास कमरे और पीजी" else "🏠 Rooms & PG Near You"
            "marketplace_deals" -> if (isHindi) "🛒 स्थानीय मार्केटप्लेस डील्स" else "🛒 Marketplace Deals"
            "latest_hindi_news" -> if (isHindi) "📰 ताज़ा हिंदी समाचार" else "📰 Latest Hindi News"
            "social_buzz" -> if (isHindi) "👥 सामाजिक चर्चा व पोस्ट" else "👥 Social Feed & Updates"
            "view_all" -> if (isHindi) "सभी देखें" else "View All"
            "call_owner" -> if (isHindi) "कॉल करें" else "Call Owner"
            "chat_seller" -> if (isHindi) "चैट करें" else "Chat Now"
            "rent_per_month" -> if (isHindi) "/ माह" else "/ month"
            "deposit" -> if (isHindi) "सुरक्षा राशि: " else "Deposit: "
            "furnished" -> if (isHindi) "फर्निश्ड" else "Furnished"
            "all_areas" -> if (isHindi) "सभी क्षेत्र" else "All Areas"
            "my_area" -> if (isHindi) "मेरा क्षेत्र" else "My Area"
            "my_district" -> if (isHindi) "मेरा जिला" else "My District"
            "my_state" -> if (isHindi) "मेरा राज्य" else "My State"
            "all_india" -> if (isHindi) "संपूर्ण भारत" else "All India"
            "submit_news" -> if (isHindi) "📝 समाचार भेजें" else "📝 Submit News"
            "run_ai_news" -> if (isHindi) "⚡ ऑटो न्यूज़ फेच" else "⚡ Auto Fetch News"
            "verified_badge" -> if (isHindi) "✓ सत्यापित" else "✓ Verified"
            "admin_panel" -> if (isHindi) "🛡️ एडमिन डैशबोर्ड" else "🛡️ Admin Panel"
            else -> key
        }
    }
}
