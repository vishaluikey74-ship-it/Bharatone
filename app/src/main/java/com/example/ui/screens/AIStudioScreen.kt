package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun AIStudioScreen(
    currentTool: AIStudioTool,
    isLoading: Boolean,
    statusMessage: String,
    language: String,
    lastMusic: GeneratedMusicResult?,
    lastImage: GeneratedImageResult?,
    lastVideo: GeneratedVideoResult?,
    mapsResult: Pair<String, List<GroundedPlaceItem>>?,
    searchResult: Pair<String, List<GroundedWebSource>>?,
    onSelectTool: (AIStudioTool) -> Unit,
    onGenerateMusic: (String, Boolean) -> Unit,
    onGenerateOrEditImage: (String, String, Bitmap?) -> Unit,
    onGenerateVideoFromText: (String, String) -> Unit,
    onAnimateImageToVideo: (String, Bitmap, String) -> Unit,
    onQueryMaps: (String) -> Unit,
    onQuerySearch: (String) -> Unit,
    onBack: () -> Unit,
    onReportAIContent: (String, String, String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val isHindi = language == "hi"
    val context = LocalContext.current
    var showReportAIDialog by remember { mutableStateOf<String?>(null) }

    if (showReportAIDialog != null) {
        ReportAIDialog(
            aiContentType = showReportAIDialog!!,
            isHindi = isHindi,
            onDismiss = { showReportAIDialog = null },
            onSubmit = { reason, details ->
                onReportAIContent(showReportAIDialog!!, reason, details)
                Toast.makeText(
                    context,
                    if (isHindi) "आपकी रिपोर्ट सफलतापूर्वक दर्ज कर ली गई है।" else "Report submitted for review. Thank you.",
                    Toast.LENGTH_LONG
                ).show()
                showReportAIDialog = null
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Banner / Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp,
            border = BorderStroke(1.dp, SlateBorder.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(38.dp)
                            .background(SlateBorderLight, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = SlateTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isHindi) "भारत एआई स्टूडियो" else "Bharat AI Studio Hub",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SaffronPrimary
                            ) {
                                Text(
                                    text = "GEMINI & VEO",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isHindi) "संगीत, इमेज, वियो वीडियो, मैप्स व सर्च डेटा" else "Music, Image, Veo Video, Maps & Search Grounding",
                            fontSize = 11.5.sp,
                            color = SlateTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tool selection chips row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(AIStudioTool.values()) { tool ->
                        val isSelected = currentTool == tool
                        val toolIcon = when (tool) {
                            AIStudioTool.MUSIC_GEN -> Icons.Default.MusicNote
                            AIStudioTool.IMAGE_CREATE_EDIT -> Icons.Default.AutoFixHigh
                            AIStudioTool.TEXT_TO_VIDEO -> Icons.Default.Videocam
                            AIStudioTool.MAPS_GROUNDING -> Icons.Default.LocationOn
                            AIStudioTool.IMAGE_TO_VIDEO -> Icons.Default.MovieCreation
                            AIStudioTool.SEARCH_GROUNDING -> Icons.Default.Search
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) SaffronContainer else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) SaffronPrimary else SlateBorder
                            ),
                            modifier = Modifier
                                .testTag("tool_chip_${tool.id}")
                                .clickable { onSelectTool(tool) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = toolIcon,
                                    contentDescription = null,
                                    tint = if (isSelected) SaffronPrimary else SlateTextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Column {
                                    Text(
                                        text = if (isHindi) tool.titleHi else tool.titleEn,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) SaffronDark else SlateTextPrimary,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = tool.modelName,
                                        fontSize = 8.5.sp,
                                        color = if (isSelected) SaffronPrimary else SlateTextSecondary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Tool Content Screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (currentTool) {
                AIStudioTool.MUSIC_GEN -> MusicGenerationSection(
                    isLoading = isLoading,
                    statusMessage = statusMessage,
                    language = language,
                    lastMusic = lastMusic,
                    onGenerate = onGenerateMusic,
                    onReportClick = { showReportAIDialog = "AI Music (Lyria)" }
                )
                AIStudioTool.IMAGE_CREATE_EDIT -> ImageCreateEditSection(
                    isLoading = isLoading,
                    statusMessage = statusMessage,
                    language = language,
                    lastImage = lastImage,
                    onGenerateOrEdit = onGenerateOrEditImage,
                    onReportClick = { showReportAIDialog = "AI Image (Imagen)" }
                )
                AIStudioTool.TEXT_TO_VIDEO -> TextToVideoSection(
                    isLoading = isLoading,
                    statusMessage = statusMessage,
                    language = language,
                    lastVideo = lastVideo,
                    onGenerateVideo = onGenerateVideoFromText,
                    onReportClick = { showReportAIDialog = "AI Video (Veo Text-to-Video)" }
                )
                AIStudioTool.MAPS_GROUNDING -> MapsGroundingSection(
                    isLoading = isLoading,
                    statusMessage = statusMessage,
                    language = language,
                    mapsResult = mapsResult,
                    onQueryMaps = onQueryMaps
                )
                AIStudioTool.IMAGE_TO_VIDEO -> ImageToVideoSection(
                    isLoading = isLoading,
                    statusMessage = statusMessage,
                    language = language,
                    lastVideo = lastVideo,
                    onAnimate = onAnimateImageToVideo,
                    onReportClick = { showReportAIDialog = "AI Video (Veo Photo Animation)" }
                )
                AIStudioTool.SEARCH_GROUNDING -> SearchGroundingSection(
                    isLoading = isLoading,
                    statusMessage = statusMessage,
                    language = language,
                    searchResult = searchResult,
                    onQuerySearch = onQuerySearch
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 1. MUSIC GENERATION SECTION (lyria-3-clip-preview / lyria-3-pro-preview)
// -------------------------------------------------------------
@Composable
fun MusicGenerationSection(
    isLoading: Boolean,
    statusMessage: String,
    language: String,
    lastMusic: GeneratedMusicResult?,
    onGenerate: (String, Boolean) -> Unit,
    onReportClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val isHindi = language == "hi"
    var prompt by remember { mutableStateOf("Bhopal city morning folk music with acoustic sitar and modern beat") }
    var isFullLength by remember { mutableStateOf(false) } // false = lyria-3-clip-preview (up to 30s), true = lyria-3-pro-preview (full track)
    var isPlaying by remember { mutableStateOf(false) }

    val presetPrompts = listOf(
        "Bhopal smart city theme jingle with shehnai & electronic beats",
        "Relaxing monsoon rain acoustic ambient guitar in Madhya Pradesh",
        "Fast energetic Mandi marketplace promo music with dhol beats",
        "Peaceful hostel student study ambient soundtrack 432Hz"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(SaffronContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = SaffronPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isHindi) "लाइरिया 3 संगीत निर्माण" else "Lyria 3 Audio & Music Generator",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = if (isFullLength) "Model: lyria-3-pro-preview (Full Track)" else "Model: lyria-3-clip-preview (Up to 30s)",
                                fontSize = 11.5.sp,
                                color = SaffronPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mode Toggle (Clip vs Full Track)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SlateBorderLight, RoundedCornerShape(10.dp))
                            .padding(3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (!isFullLength) MaterialTheme.colorScheme.surface else Color.Transparent)
                                .clickable { isFullLength = false }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isHindi) "शॉर्ट क्लिप (30s)" else "Short Clip (30s)",
                                fontSize = 12.sp,
                                fontWeight = if (!isFullLength) FontWeight.Bold else FontWeight.Normal,
                                color = if (!isFullLength) SaffronPrimary else SlateTextSecondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isFullLength) MaterialTheme.colorScheme.surface else Color.Transparent)
                                .clickable { isFullLength = true }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isHindi) "पूरा ट्रैक (Pro)" else "Full Track (Pro)",
                                fontSize = 12.sp,
                                fontWeight = if (isFullLength) FontWeight.Bold else FontWeight.Normal,
                                color = if (isFullLength) SaffronPrimary else SlateTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { prompt = it },
                        label = { Text(if (isHindi) "संगीत प्रॉम्प्ट लिखें" else "Enter Music Prompt") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_music_prompt"),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isHindi) "सुझाए गए प्रॉम्प्ट्स:" else "Suggested Prompts:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    presetPrompts.forEach { p ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SlateBorderLight.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clickable { prompt = p }
                        ) {
                            Text(
                                text = "✨ $p",
                                fontSize = 11.sp,
                                color = SlateTextPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onGenerate(prompt, isFullLength) },
                        enabled = !isLoading && prompt.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_generate_music")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isHindi) "संगीत जनरेट हो रहा है..." else "Generating Lyria Audio...")
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "लाइरिया 3 से संगीत बनाएं" else "Generate with Lyria 3",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Generated Result Player Card
        item {
            val music = lastMusic ?: GeneratedMusicResult(
                title = "Bhopal Lake Sunset Melody",
                modelUsed = if (isFullLength) "lyria-3-pro-preview" else "lyria-3-clip-preview",
                durationSeconds = if (isFullLength) 90 else 30,
                prompt = prompt,
                genre = "Indian Folk Fusion"
            )

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SaffronPrimary.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "जनरेटेड ऑडियो प्लेयर" else "Lyria Audio Track Player",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ForestGreenContainer
                        ) {
                            Text(
                                text = music.modelUsed,
                                color = ForestGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Audio Waveform Visualization Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .background(NavyHeroGradient, RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val heights = listOf(24, 40, 16, 50, 32, 48, 20, 56, 38, 28, 44, 20, 52, 34, 46, 22, 50, 30, 42, 18, 48, 36)
                            heights.forEach { h ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(if (isPlaying) (h * (0.8f + (Math.random() * 0.4f).toFloat())).dp else h.dp)
                                        .background(
                                            if (isPlaying) SaffronPrimary else Color.White.copy(alpha = 0.6f),
                                            RoundedCornerShape(4.dp)
                                        )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = music.title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Genre: ${music.genre} • Duration: ${music.durationSeconds}s",
                        fontSize = 12.sp,
                        color = SlateTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                isPlaying = !isPlaying
                                Toast.makeText(context, if (isPlaying) "Playing Lyria Track..." else "Playback Paused", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .background(SaffronPrimary, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = onReportClick,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = BreakingNewsRed),
                                modifier = Modifier.testTag("report_ai_music_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Flag, contentDescription = "Report", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isHindi) "रिपोर्ट" else "Report", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    Toast.makeText(context, "Track saved to listing assets!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isHindi) "सेव करें" else "Save Track", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    Toast.makeText(context, "Attached audio jingle to Room / Marketplace listing!", Toast.LENGTH_LONG).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NavyDark)
                            ) {
                                Icon(imageVector = Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isHindi) "लिस्टिंग में जोड़ें" else "Attach to Listing", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. CREATE & EDIT IMAGES (gemini-3.1-flash-image-preview)
// -------------------------------------------------------------
@Composable
fun ImageCreateEditSection(
    isLoading: Boolean,
    statusMessage: String,
    language: String,
    lastImage: GeneratedImageResult?,
    onGenerateOrEdit: (String, String, Bitmap?) -> Unit,
    onReportClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val isHindi = language == "hi"
    var isEditMode by remember { mutableStateOf(false) }
    var prompt by remember { mutableStateOf("Clean spacious 2BHK furnished room in MP Nagar Bhopal with bright balcony sunlight and wooden wardrobe") }
    var selectedAspectRatio by remember { mutableStateOf("1:1") } // 1:1, 16:9, 4:3, 3:4

    // Mock base bitmap for editing
    val sampleBitmap = remember {
        val bmp = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val paint = Paint().apply {
            color = android.graphics.Color.rgb(30, 41, 59)
        }
        canvas.drawRect(0f, 0f, 400f, 300f, paint)
        paint.color = android.graphics.Color.rgb(255, 153, 51)
        paint.textSize = 24f
        canvas.drawText("Sample Room / Product Photo", 40f, 150f, paint)
        bmp
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(SaffronContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoFixHigh,
                                contentDescription = null,
                                tint = SaffronPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isHindi) "इमेज निर्माण व संपादन" else "Gemini 3.1 Flash Image Suite",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Model: gemini-3.1-flash-image-preview",
                                fontSize = 11.5.sp,
                                color = SaffronPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mode switch: Create vs Edit
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SlateBorderLight, RoundedCornerShape(10.dp))
                            .padding(3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (!isEditMode) MaterialTheme.colorScheme.surface else Color.Transparent)
                                .clickable { isEditMode = false }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isHindi) "✨ नई इमेज बनाएं" else "✨ Create New Image",
                                fontSize = 12.sp,
                                fontWeight = if (!isEditMode) FontWeight.Bold else FontWeight.Normal,
                                color = if (!isEditMode) SaffronPrimary else SlateTextSecondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isEditMode) MaterialTheme.colorScheme.surface else Color.Transparent)
                                .clickable { isEditMode = true }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isHindi) "🖌️ इमेज एडिट करें" else "🖌️ Edit Existing Image",
                                fontSize = 12.sp,
                                fontWeight = if (isEditMode) FontWeight.Bold else FontWeight.Normal,
                                color = if (isEditMode) SaffronPrimary else SlateTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Aspect Ratio Selector
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isHindi) "अनुपात:" else "Aspect Ratio:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextSecondary
                        )
                        listOf("1:1", "16:9", "4:3", "3:4").forEach { ratio ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (selectedAspectRatio == ratio) SaffronContainer else SlateBorderLight,
                                border = BorderStroke(
                                    1.dp,
                                    if (selectedAspectRatio == ratio) SaffronPrimary else Color.Transparent
                                ),
                                modifier = Modifier.clickable { selectedAspectRatio = ratio }
                            ) {
                                Text(
                                    text = ratio,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedAspectRatio == ratio) SaffronDark else SlateTextPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { prompt = it },
                        label = {
                            Text(
                                if (isEditMode) (if (isHindi) "संपादन निर्देश (जैसे: कमरे में गर्म लाइटिंग जोड़ें)" else "Edit Instructions (e.g. Add warm studio lighting)")
                                else (if (isHindi) "इमेज प्रॉम्प्ट लिखें" else "Enter Image Generation Prompt")
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_image_prompt"),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            onGenerateOrEdit(prompt, selectedAspectRatio, if (isEditMode) sampleBitmap else null)
                        },
                        enabled = !isLoading && prompt.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_generate_image")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isHindi) "इमेज तैयार हो रही है..." else "Processing with Gemini 3.1...")
                        } else {
                            Icon(imageVector = Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEditMode) (if (isHindi) "इमेज संपादित करें" else "Edit Image with Gemini 3.1")
                                       else (if (isHindi) "इमेज बनाएं (1K)" else "Generate Image (1K Res)"),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Generated Image Preview Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "इमेज प्रीव्यू" else "Gemini 3.1 Generated Image",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SaffronContainer
                        ) {
                            Text(
                                text = "gemini-3.1-flash-image-preview",
                                color = SaffronDark,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Visual Representation Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF334155))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (lastImage?.bitmap != null) {
                            androidx.compose.foundation.Image(
                                bitmap = lastImage.bitmap.asImageBitmap(),
                                contentDescription = "Generated Image",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = SaffronPrimary,
                                    modifier = Modifier.size(54.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = prompt.take(60) + "...",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "1K Resolution • Aspect Ratio $selectedAspectRatio",
                                    color = SlateTextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = onReportClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BreakingNewsRed),
                            modifier = Modifier.weight(0.9f).testTag("report_ai_image_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Flag, contentDescription = "Report", modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(if (isHindi) "रिपोर्ट" else "Report", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "Image saved to gallery!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.1f)
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isHindi) "डाउनलोड" else "Download", fontSize = 11.5.sp)
                        }

                        Button(
                            onClick = {
                                Toast.makeText(context, "Added as cover photo for listing!", Toast.LENGTH_LONG).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isHindi) "कवर फोटो" else "Use in Listing", fontSize = 11.5.sp)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. GENERATE VIDEO FROM TEXT (Veo 3: veo-3.1-fast-generate-preview)
// -------------------------------------------------------------
@Composable
fun TextToVideoSection(
    isLoading: Boolean,
    statusMessage: String,
    language: String,
    lastVideo: GeneratedVideoResult?,
    onGenerateVideo: (String, String) -> Unit,
    onReportClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val isHindi = language == "hi"
    var prompt by remember { mutableStateOf("Cinematic drone flyover of Bhopal Upper Lake and smart city boulevard at golden hour with realistic reflections") }
    var selectedAspectRatio by remember { mutableStateOf("16:9") } // "16:9" or "9:16"
    var isPlayingVideo by remember { mutableStateOf(false) }

    val videoPrompts = listOf(
        "Cinematic slow drone view of Bhopal VIP Road and Upper Lake sunset",
        "Hyper-realistic 3D walkthrough of 2BHK luxury furnished apartment in MP Nagar",
        "Vibrant Indore Chhappan Dukan street food bazaar with evening steam and lights"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(NavyHeroGradient, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isHindi) "वियो 3 टेक्स्ट से वीडियो" else "Veo 3 Text-to-Video Generator",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Model: veo-3.1-fast-generate-preview",
                                fontSize = 11.5.sp,
                                color = SaffronPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Aspect Ratio Selector (16:9 or 9:16)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SlateBorderLight, RoundedCornerShape(10.dp))
                            .padding(3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedAspectRatio == "16:9") MaterialTheme.colorScheme.surface else Color.Transparent)
                                .clickable { selectedAspectRatio = "16:9" }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isHindi) "16:9 लैंडस्केप (YouTube/TV)" else "16:9 Landscape (1080p)",
                                fontSize = 12.sp,
                                fontWeight = if (selectedAspectRatio == "16:9") FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedAspectRatio == "16:9") SaffronPrimary else SlateTextSecondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedAspectRatio == "9:16") MaterialTheme.colorScheme.surface else Color.Transparent)
                                .clickable { selectedAspectRatio = "9:16" }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isHindi) "9:16 पोर्ट्रेट (Reels/Shorts)" else "9:16 Portrait (Reels/Shorts)",
                                fontSize = 12.sp,
                                fontWeight = if (selectedAspectRatio == "9:16") FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedAspectRatio == "9:16") SaffronPrimary else SlateTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { prompt = it },
                        label = { Text(if (isHindi) "वीडियो सीन प्रॉम्प्ट लिखें" else "Enter Video Scene Prompt") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_video_prompt"),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isHindi) "सुझाए गए वियो प्रॉम्प्ट्स:" else "Suggested Veo Prompts:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    videoPrompts.forEach { p ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SlateBorderLight.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clickable { prompt = p }
                        ) {
                            Text(
                                text = "🎬 $p",
                                fontSize = 11.sp,
                                color = SlateTextPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onGenerateVideo(prompt, selectedAspectRatio) },
                        enabled = !isLoading && prompt.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_generate_video")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isHindi) "वियो वीडियो तैयार हो रहा है..." else "Generating with Veo 3...")
                        } else {
                            Icon(imageVector = Icons.Default.MovieFilter, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "वियो 3 से वीडियो जनरेट करें" else "Generate Video with Veo 3",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Generated Video Player Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "वियो वीडियो प्रीव्यू" else "Veo 3 Cinematic Video Player",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = NavyDark
                        ) {
                            Text(
                                text = "veo-3.1-fast-generate-preview",
                                color = Color.White,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Video Frame Simulation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (selectedAspectRatio == "9:16") 280.dp else 190.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF020617), Color(0xFF0F172A), Color(0xFF1E293B))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    isPlayingVideo = !isPlayingVideo
                                    Toast.makeText(context, if (isPlayingVideo) "Playing Veo Video..." else "Paused", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(SaffronPrimary.copy(alpha = 0.9f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isPlayingVideo) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = prompt.take(65) + "...",
                                color = Color.White,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Aspect Ratio: $selectedAspectRatio • 720p HD",
                                color = SaffronLight,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = onReportClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BreakingNewsRed),
                            modifier = Modifier.weight(0.9f).testTag("report_ai_video_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Flag, contentDescription = "Report", modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(if (isHindi) "रिपोर्ट" else "Report", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "Veo MP4 Video saved to downloads!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.1f)
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isHindi) "डाउनलोड MP4" else "Download MP4", fontSize = 11.5.sp)
                        }

                        Button(
                            onClick = {
                                Toast.makeText(context, "Attached video tour to listing!", Toast.LENGTH_LONG).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isHindi) "शेयर / लिस्टिंग" else "Share Video", fontSize = 11.5.sp)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. GOOGLE MAPS GROUNDING SECTION (gemini-3.5-flash with googleMaps tool)
// -------------------------------------------------------------
@Composable
fun MapsGroundingSection(
    isLoading: Boolean,
    statusMessage: String,
    language: String,
    mapsResult: Pair<String, List<GroundedPlaceItem>>?,
    onQueryMaps: (String) -> Unit
) {
    val context = LocalContext.current
    val isHindi = language == "hi"
    var query by remember { mutableStateOf("Best student PG and hostels near MP Nagar Zone 2 Bhopal with wifi and mess") }

    val presetMapsQueries = listOf(
        "Top boys & girls PG hostels near MP Nagar Zone II Bhopal",
        "Government coaching centers near Vijay Nagar Indore",
        "District hospitals & 24x7 emergency medical centers in Balaghat",
        "Key bus stands and railway junction routes in Bhopal"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(ForestGreenContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isHindi) "गूगल मैप्स ग्राउंडिंग" else "Google Maps Grounded Intelligence",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Model: gemini-3.5-flash (with googleMaps tool)",
                                fontSize = 11.5.sp,
                                color = ForestGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text(if (isHindi) "स्थान / पीजी / अस्पताल खोजें" else "Search Grounded Places / Hostels / Routes") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_maps_query"),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isHindi) "लोकप्रिय मैप्स सर्च:" else "Popular Grounded Queries:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    presetMapsQueries.forEach { q ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SlateBorderLight.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clickable { query = q }
                        ) {
                            Text(
                                text = "📍 $q",
                                fontSize = 11.sp,
                                color = SlateTextPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onQueryMaps(query) },
                        enabled = !isLoading && query.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_query_maps")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isHindi) "मैप्स डेटा फेच हो रहा है..." else "Querying Google Maps Tool...")
                        } else {
                            Icon(imageVector = Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "सत्यापित मैप्स डेटा खोजें" else "Search with Google Maps Tool",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Grounded Results List
        if (mapsResult == null) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, SlateBorder.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isHindi) "खोजने के लिए ऊपर दिए गए बटन को दबाएं" else "Search Grounded Places",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isHindi) "गूगल मैप्स टूल से लाइव सत्यापित स्थान प्राप्त करें।" else "Use the Google Maps tool above to fetch live location data.",
                            fontSize = 12.sp,
                            color = SlateTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            val (summaryText, placeList) = mapsResult
            if (placeList.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, SlateBorder.copy(alpha = 0.7f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = SlateTextSecondary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isHindi) "कोई परिणाम नहीं मिला, कृपया पुनः प्रयास करें" else "No results found, please try again",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SlateTextPrimary,
                                textAlign = TextAlign.Center
                            )
                            if (summaryText.isNotBlank() && summaryText != "No results found, please try again") {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = summaryText,
                                    fontSize = 12.sp,
                                    color = SlateTextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            } else {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isHindi) "मैप्स ग्राउंडिंग सारांश" else "Grounded Maps Analysis",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = summaryText,
                                fontSize = 13.sp,
                                color = SlateTextPrimary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = if (isHindi) "स्थान व संपर्क (${placeList.size}):" else "Grounded Locations (${placeList.size}):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SlateTextPrimary
                    )
                }

                items(placeList) { place ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, SlateBorder.copy(alpha = 0.7f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(ForestGreenContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = ForestGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = place.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = place.address,
                                    fontSize = 11.5.sp,
                                    color = SlateTextSecondary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (place.rating != null || place.reviewCount != null || place.distance != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (place.rating != null) {
                                            Text(
                                                text = "⭐ ${place.rating}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SaffronDark
                                            )
                                        }
                                        if (place.reviewCount != null) {
                                            Text(
                                                text = " (${place.reviewCount} reviews)",
                                                fontSize = 10.5.sp,
                                                color = SlateTextSecondary
                                            )
                                        }
                                        if (place.distance != null) {
                                            Text(
                                                text = if (place.rating != null || place.reviewCount != null) " • ${place.distance}" else place.distance,
                                                fontSize = 10.5.sp,
                                                color = ForestGreen,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Opening Google Maps Directions for ${place.title}...", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(SlateBorderLight, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Navigation,
                                    contentDescription = "Navigate",
                                    tint = ForestGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. ANIMATE IMAGES INTO VIDEO (Veo Photo to Video: veo-3.1-fast-generate-preview)
// -------------------------------------------------------------
@Composable
fun ImageToVideoSection(
    isLoading: Boolean,
    statusMessage: String,
    language: String,
    lastVideo: GeneratedVideoResult?,
    onAnimate: (String, Bitmap, String) -> Unit,
    onReportClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val isHindi = language == "hi"
    var prompt by remember { mutableStateOf("Slow cinematic dolly forward with warm golden hour sunbeams streaming across the room, gentle curtain movement") }
    var selectedAspectRatio by remember { mutableStateOf("16:9") } // "16:9" or "9:16"

    val sampleListingPhoto = remember {
        val bmp = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val paint = Paint().apply {
            color = android.graphics.Color.rgb(15, 23, 42)
        }
        canvas.drawRect(0f, 0f, 400f, 300f, paint)
        paint.color = android.graphics.Color.rgb(255, 153, 51)
        paint.textSize = 22f
        canvas.drawText("Room / Property Photo", 50f, 150f, paint)
        bmp
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(NavyHeroGradient, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Movie,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isHindi) "फोटो को वियो वीडियो में एनिमेट करें" else "Animate Photos into Veo Video",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Model: veo-3.1-fast-generate-preview",
                                fontSize = 11.5.sp,
                                color = SaffronPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Selected Photo Box
                    Text(
                        text = if (isHindi) "चयनित स्रोत फोटो:" else "Selected Source Photo:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NavyDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = SaffronPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Ready to Animate: Room_Balcony_01.jpg",
                                    color = Color.White,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tap to choose different photo from listing",
                                    color = SlateTextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Aspect Ratio Selector (16:9 or 9:16)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SlateBorderLight, RoundedCornerShape(10.dp))
                            .padding(3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedAspectRatio == "16:9") MaterialTheme.colorScheme.surface else Color.Transparent)
                                .clickable { selectedAspectRatio = "16:9" }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "16:9 Landscape",
                                fontSize = 12.sp,
                                fontWeight = if (selectedAspectRatio == "16:9") FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedAspectRatio == "16:9") SaffronPrimary else SlateTextSecondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedAspectRatio == "9:16") MaterialTheme.colorScheme.surface else Color.Transparent)
                                .clickable { selectedAspectRatio = "9:16" }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "9:16 Portrait / Reels",
                                fontSize = 12.sp,
                                fontWeight = if (selectedAspectRatio == "9:16") FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedAspectRatio == "9:16") SaffronPrimary else SlateTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { prompt = it },
                        label = { Text(if (isHindi) "मोशन व कैमरा एनिमेशन प्रॉम्प्ट" else "Camera Motion & Animation Prompt") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_animate_prompt"),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onAnimate(prompt, sampleListingPhoto, selectedAspectRatio) },
                        enabled = !isLoading && prompt.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_animate_image")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isHindi) "वियो वीडियो तैयार हो रहा है..." else "Veo Animating Photo...")
                        } else {
                            Icon(imageVector = Icons.Default.MovieCreation, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "फोटो को वियो वीडियो में बदलें" else "Animate Photo into Veo Video",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Output Result Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "एनिमेटेड वीडियो प्रीव्यू" else "Veo Animated Output",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = NavyDark
                        ) {
                            Text(
                                text = "veo-3.1-fast-generate-preview",
                                color = Color.White,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(NavyHeroGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = "Play",
                                tint = SaffronPrimary,
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Photo Animated with Veo 3 Engine",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Aspect Ratio: $selectedAspectRatio • Motion: Dolly Zoom In",
                                color = SlateTextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onReportClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BreakingNewsRed),
                            modifier = Modifier.weight(1f).testTag("report_ai_animate_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Flag, contentDescription = "Report", modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isHindi) "रिपोर्ट" else "Report", fontSize = 11.5.sp)
                        }

                        Button(
                            onClick = {
                                Toast.makeText(context, "Attached animated video tour to room listing!", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(2f)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isHindi) "लिस्टिंग में जोड़ें" else "Attach Video")
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 6. GOOGLE SEARCH GROUNDING SECTION (gemini-3.5-flash with googleSearch tool)
// -------------------------------------------------------------
@Composable
fun SearchGroundingSection(
    isLoading: Boolean,
    statusMessage: String,
    language: String,
    searchResult: Pair<String, List<GroundedWebSource>>?,
    onQuerySearch: (String) -> Unit
) {
    val context = LocalContext.current
    val isHindi = language == "hi"
    var query by remember { mutableStateOf("Latest Madhya Pradesh Government job recruitment notifications and MPPSC exam dates 2026") }

    val presetSearchQueries = listOf(
        "Latest MP Patwari and Police Constable vacancy notification 2026",
        "Current soybean, wheat and garlic mandi bhav in Balaghat & Sehore",
        "MP Ladli Behna Yojana next installment and KYC guideline update",
        "Fact check: New government free laptop distribution eligibility rule"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(SaffronContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = SaffronPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isHindi) "गूगल सर्च ग्राउंडिंग व फैक्ट-चेक" else "Google Search Grounded Fact Check",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Model: gemini-3.5-flash (with googleSearch tool)",
                                fontSize = 11.5.sp,
                                color = SaffronPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text(if (isHindi) "भर्ती, मंडी भाव या समाचार फैक्ट-चेक करें" else "Search / Fact Check with Live Google Web Data") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_search_query"),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isHindi) "लोकप्रिय ग्राउंडेड फैक्ट-चेक:" else "Popular Grounded Fact Checks:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    presetSearchQueries.forEach { q ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SlateBorderLight.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clickable { query = q }
                        ) {
                            Text(
                                text = "🔍 $q",
                                fontSize = 11.sp,
                                color = SlateTextPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onQuerySearch(query) },
                        enabled = !isLoading && query.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_query_search")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isHindi) "सर्च डेटा फेच हो रहा है..." else "Querying Google Search Tool...")
                        } else {
                            Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "गूगल सर्च से लाइव जांचें" else "Fact Check with Google Search Tool",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Grounded Results
        if (searchResult == null) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, SlateBorder.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = SaffronPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isHindi) "जांचने के लिए ऊपर दिए गए बटन को दबाएं" else "Fact Check with Google Search",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isHindi) "गूगल सर्च टूल से लाइव वेब स्रोतों और आधिकारिक सूचनाओं की जांच करें।" else "Use the Google Search tool above to verify facts with live web sources.",
                            fontSize = 12.sp,
                            color = SlateTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            val (summaryText, sourceList) = searchResult
            if (sourceList.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, SlateBorder.copy(alpha = 0.7f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = SlateTextSecondary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isHindi) "कोई परिणाम नहीं मिला, कृपया पुनः प्रयास करें" else "No results found, please try again",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SlateTextPrimary,
                                textAlign = TextAlign.Center
                            )
                            if (summaryText.isNotBlank() && summaryText != "No results found, please try again") {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = summaryText,
                                    fontSize = 12.sp,
                                    color = SlateTextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            } else {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, SaffronPrimary.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isHindi) "सर्च ग्राउंडिंग परिणाम" else "Grounded Search Result",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = summaryText,
                                fontSize = 13.sp,
                                color = SlateTextPrimary,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = if (isHindi) "वेब संदर्भ व स्रोत (${sourceList.size}):" else "Web Citations & Sources (${sourceList.size}):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SlateTextPrimary
                    )
                }

                items(sourceList) { src ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, SlateBorder.copy(alpha = 0.7f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SlateBorderLight
                                ) {
                                    Text(
                                        text = src.domain,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SlateTextSecondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Web Source",
                                    tint = ForestGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = src.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = SlateTextPrimary
                            )

                            if (!src.snippet.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = src.snippet,
                                    fontSize = 11.5.sp,
                                    color = SlateTextSecondary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = src.url,
                                fontSize = 10.5.sp,
                                color = SaffronDark,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.clickable {
                                    Toast.makeText(context, "Opening source citation: ${src.url}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportAIDialog(
    aiContentType: String,
    isHindi: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var selectedReason by remember { mutableStateOf(if (isHindi) "अनुचित या आपत्तिजनक सामग्री" else "Inappropriate or Offensive") }
    var details by remember { mutableStateOf("") }
    val reasons = if (isHindi) listOf(
        "अनुचित या आपत्तिजनक सामग्री",
        "भ्रामक या डीपफेक सामग्री",
        "कॉपीराइट उल्लंघन",
        "स्पैम या अवांछित परिणाम"
    ) else listOf(
        "Inappropriate or Offensive",
        "Misinformation or Deepfake",
        "Copyright Infringement",
        "Spam or Low Quality"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Flag, contentDescription = null, tint = BreakingNewsRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isHindi) "AI सामग्री रिपोर्ट करें" else "Report AI Output",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isHindi) "प्रकार: $aiContentType" else "Content Type: $aiContentType",
                    fontSize = 12.sp,
                    color = SlateTextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (isHindi) "रिपोर्ट का कारण चुनें:" else "Select Reason:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                reasons.forEach { reason ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason }
                            .padding(vertical = 2.dp)
                    ) {
                        RadioButton(
                            selected = selectedReason == reason,
                            onClick = { selectedReason = reason }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = reason, fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    placeholder = { Text(if (isHindi) "अतिरिक्त विवरण (वैकब्लपक)..." else "Additional details (optional)...", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(selectedReason, details) },
                colors = ButtonDefaults.buttonColors(containerColor = BreakingNewsRed)
            ) {
                Text(text = if (isHindi) "रिपोर्ट सबमिट करें" else "Submit Report")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = if (isHindi) "रद्द करें" else "Cancel")
            }
        }
    )
}
