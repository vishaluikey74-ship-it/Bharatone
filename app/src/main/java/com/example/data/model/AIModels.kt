package com.example.data.model

import android.graphics.Bitmap

enum class AIStudioTool(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val subtitleEn: String,
    val subtitleHi: String,
    val modelName: String,
    val iconTag: String
) {
    MUSIC_GEN(
        id = "music_gen",
        titleEn = "Generate Music",
        titleHi = "संगीत बनाएं",
        subtitleEn = "Lyria 3 Audio Generation (Clips & Tracks)",
        subtitleHi = "लाइरिया 3 संगीत व ऑडियो जनरेटर",
        modelName = "lyria-3-clip-preview",
        iconTag = "music_note"
    ),
    IMAGE_CREATE_EDIT(
        id = "image_create_edit",
        titleEn = "Create & Edit Images",
        titleHi = "इमेज बनाएं व एडिट करें",
        subtitleEn = "Text to image & prompt editing",
        subtitleHi = "टेक्स्ट से इमेज निर्माण व संपादन",
        modelName = "gemini-3.1-flash-image-preview",
        iconTag = "image_edit_auto"
    ),
    TEXT_TO_VIDEO(
        id = "text_to_video",
        titleEn = "Generate Video from Text",
        titleHi = "टेक्स्ट से वीडियो बनाएं",
        subtitleEn = "Veo 3 high-definition video generation",
        subtitleHi = "वियो 3 सिनेमाई वीडियो जनरेटर",
        modelName = "veo-3.1-fast-generate-preview",
        iconTag = "video_spark"
    ),
    MAPS_GROUNDING(
        id = "maps_grounding",
        titleEn = "Google Maps Data",
        titleHi = "गूगल मैप्स डेटा व स्थान",
        subtitleEn = "Live grounded places, hostels & routes",
        subtitleHi = "सत्यापित पीजी, कोचिंग व अस्पताल खोजें",
        modelName = "gemini-3.5-flash",
        iconTag = "google_pin"
    ),
    IMAGE_TO_VIDEO(
        id = "image_to_video",
        titleEn = "Animate Images into Video",
        titleHi = "फोटो को वीडियो में एनिमेट करें",
        subtitleEn = "Veo photo-to-motion cinematic animation",
        subtitleHi = "तस्वीरों को जीवंत वीडियो बनाएं",
        modelName = "veo-3.1-fast-generate-preview",
        iconTag = "movie"
    ),
    SEARCH_GROUNDING(
        id = "search_grounding",
        titleEn = "Google Search Data",
        titleHi = "गूगल सर्च डेटा व तथ्य जांच",
        subtitleEn = "Live grounded search with web citations",
        subtitleHi = "ताज़ा भर्ती व लाइव फैक्ट-चेक",
        modelName = "gemini-3.5-flash",
        iconTag = "google"
    )
}

// Data structures for results
data class GroundedPlaceItem(
    val title: String,
    val address: String,
    val rating: Double? = null,
    val reviewCount: Int? = null,
    val mapUri: String? = null,
    val category: String,
    val distance: String? = null
)

data class GroundedWebSource(
    val title: String,
    val url: String,
    val domain: String,
    val snippet: String? = null
)

data class GeneratedMusicResult(
    val title: String,
    val modelUsed: String,
    val durationSeconds: Int,
    val prompt: String,
    val genre: String,
    val audioBase64: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class GeneratedImageResult(
    val prompt: String,
    val isEdit: Boolean,
    val aspectRatio: String,
    val bitmap: Bitmap? = null,
    val base64Data: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class GeneratedVideoResult(
    val prompt: String,
    val modelUsed: String,
    val aspectRatio: String,
    val durationSeconds: Int = 5,
    val videoUri: String? = null,
    val isFromImage: Boolean = false,
    val thumbnailBitmap: Bitmap? = null,
    val timestamp: Long = System.currentTimeMillis()
)
