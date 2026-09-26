package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ListingDomain
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.util.ShareHelper
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.BharatViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: BharatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ProvideAdaptiveWindowInfo {
                    BharatApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun BharatApp(viewModel: BharatViewModel) {
    val context = LocalContext.current

    // Observe UI States
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val selectedState by viewModel.selectedState.collectAsStateWithLifecycle()
    val selectedDistrict by viewModel.selectedDistrict.collectAsStateWithLifecycle()
    val selectedArea by viewModel.selectedArea.collectAsStateWithLifecycle()
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    // AI Studio States
    val selectedAITool by viewModel.selectedAITool.collectAsStateWithLifecycle()
    val isAILoading by viewModel.isAILoading.collectAsStateWithLifecycle()
    val aiStatusMessage by viewModel.aiStatusMessage.collectAsStateWithLifecycle()
    val lastGeneratedMusic by viewModel.lastGeneratedMusic.collectAsStateWithLifecycle()
    val lastGeneratedImage by viewModel.lastGeneratedImage.collectAsStateWithLifecycle()
    val lastGeneratedVideo by viewModel.lastGeneratedVideo.collectAsStateWithLifecycle()
    val mapsGroundingResult by viewModel.mapsGroundingResult.collectAsStateWithLifecycle()
    val searchGroundingResult by viewModel.searchGroundingResult.collectAsStateWithLifecycle()

    // Filtered data streams
    val roomsList by viewModel.filteredRooms.collectAsStateWithLifecycle()
    val marketplaceList by viewModel.filteredMarketplace.collectAsStateWithLifecycle()
    val newsList by viewModel.filteredNews.collectAsStateWithLifecycle()
    val jobsList by viewModel.filteredJobs.collectAsStateWithLifecycle()
    val socialPosts by viewModel.socialPosts.collectAsStateWithLifecycle()
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val messagesMap by viewModel.messages.collectAsStateWithLifecycle()
    val friendRequests by viewModel.friendRequests.collectAsStateWithLifecycle()
    val savedIds by viewModel.savedListingIds.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val reports by viewModel.reports.collectAsStateWithLifecycle()
    val adminStats by viewModel.adminStats.collectAsStateWithLifecycle()
    val pipelineRuns by viewModel.pipelineExecutions.collectAsStateWithLifecycle()
    val ingestionState by viewModel.ingestionState.collectAsStateWithLifecycle()
    val ingestionLogs by viewModel.ingestionLogs.collectAsStateWithLifecycle()

    // Filter states
    val selectedPropCat by viewModel.selectedPropertyCategory.collectAsStateWithLifecycle()
    val selectedTenantPref by viewModel.selectedTenantPref.collectAsStateWithLifecycle()
    val selectedMarketCat by viewModel.selectedMarketCategory.collectAsStateWithLifecycle()
    val selectedNewsCat by viewModel.selectedNewsCategory.collectAsStateWithLifecycle()
    val selectedNewsScope by viewModel.selectedNewsScope.collectAsStateWithLifecycle()
    val selectedJobType by viewModel.selectedJobType.collectAsStateWithLifecycle()

    // Dialog & Modal states
    val selectedListing by viewModel.selectedListing.collectAsStateWithLifecycle()
    val selectedNews by viewModel.selectedNews.collectAsStateWithLifecycle()
    val selectedJob by viewModel.selectedJob.collectAsStateWithLifecycle()
    val showPipelineSimulator by viewModel.showPipelineSimulatorModal.collectAsStateWithLifecycle()
    val activeConversationId by viewModel.activeConversationId.collectAsStateWithLifecycle()
    val showLocationSelector by viewModel.showLocationSelector.collectAsStateWithLifecycle()
    val showPostModal by viewModel.showPostModal.collectAsStateWithLifecycle()
    val showSubmitNewsModal by viewModel.showSubmitNewsModal.collectAsStateWithLifecycle()
    val showCreateListingModal by viewModel.showCreateListingModal.collectAsStateWithLifecycle()
    val showNotifsModal by viewModel.showNotificationsModal.collectAsStateWithLifecycle()
    val showReportModal by viewModel.showReportModal.collectAsStateWithLifecycle()

    val unreadNotifCount = notifications.count { !it.isRead }
    val unreadChatCount = conversations.sumOf { it.unreadCount }

    Scaffold(
        topBar = {
            if (currentTab != AppTab.ADMIN && currentTab != AppTab.AI_STUDIO && activeConversationId == null) {
                BharatHeader(
                    state = selectedState,
                    district = selectedDistrict,
                    area = selectedArea,
                    language = language,
                    searchQuery = searchQuery,
                    unreadNotifCount = unreadNotifCount,
                    onLocationClick = { viewModel.toggleLocationSelector(true) },
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onLanguageToggle = { viewModel.toggleLanguage() },
                    onNotifClick = { viewModel.toggleNotifications(true) },
                    onProfileClick = { viewModel.setTab(AppTab.PROFILE) },
                    onShareClick = { ShareHelper.shareApp(context, language == "hi") },
                    onAIStudioClick = { viewModel.setTab(AppTab.AI_STUDIO) }
                )
            }
        },
        bottomBar = {
            if (currentTab != AppTab.ADMIN && activeConversationId == null) {
                BharatBottomNavBar(
                    currentTab = currentTab,
                    language = language,
                    unreadChatCount = unreadChatCount,
                    onTabSelected = { viewModel.setTab(it) },
                    onPostClick = { viewModel.togglePostModal(true) }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.HOME -> {
                    HomeScreen(
                        state = selectedState,
                        district = selectedDistrict,
                        area = selectedArea,
                        language = language,
                        rooms = roomsList,
                        marketplaceItems = marketplaceList,
                        newsList = newsList,
                        jobsList = jobsList,
                        socialPosts = socialPosts,
                        savedIds = savedIds,
                        isRefreshing = isRefreshing,
                        onRefresh = {
                            viewModel.refreshHomeContent()
                        },
                        onNavigateTab = { viewModel.setTab(it) },
                        onOpenAITool = { tool ->
                            viewModel.selectAITool(tool)
                        },
                        onListingClick = { viewModel.selectListing(it) },
                        onNewsClick = { viewModel.selectNews(it) },
                        onJobClick = { viewModel.selectJob(it) },
                        onOpenPipelineSimulator = { viewModel.togglePipelineSimulator(true) },
                        onSaveClick = { id ->
                            viewModel.toggleSave(id)
                            Toast.makeText(context, if (language == "hi") "आइटम बुकमार्क किया गया" else "Item saved to bookmarks", Toast.LENGTH_SHORT).show()
                        },
                        onCallClick = { listing ->
                            Toast.makeText(context, "Calling ${listing.sellerName} at ${listing.sellerPhone}", Toast.LENGTH_LONG).show()
                        },
                        onChatClick = { listing ->
                            viewModel.openDirectChatWithSeller(listing)
                        },
                        onLikePost = { viewModel.toggleLikePost(it) },
                        onCommentPost = { postId ->
                            viewModel.addComment(postId, "बहुत बढ़िया अपडेट!")
                            Toast.makeText(context, "Comment added", Toast.LENGTH_SHORT).show()
                        },
                        onRepostPost = { postId ->
                            Toast.makeText(context, "Post reposted to feed", Toast.LENGTH_SHORT).show()
                        },
                        onOpenReport = { type, id, title ->
                            viewModel.openReportModal(type, id, title)
                        },
                        onPostFABClick = { viewModel.togglePostModal(true) },
                        onRunAutoNews = {
                            viewModel.runAutoNews()
                            Toast.makeText(context, if (language == "hi") "ताज़ा समाचार फेच किए गए!" else "News Ingestion cycle completed!", Toast.LENGTH_SHORT).show()
                        },
                        onShareApp = {
                            ShareHelper.shareApp(context, language == "hi")
                        }
                    )
                }
                AppTab.AI_STUDIO -> {
                    AIStudioScreen(
                        currentTool = selectedAITool,
                        isLoading = isAILoading,
                        statusMessage = aiStatusMessage,
                        language = language,
                        lastMusic = lastGeneratedMusic,
                        lastImage = lastGeneratedImage,
                        lastVideo = lastGeneratedVideo,
                        mapsResult = mapsGroundingResult,
                        searchResult = searchGroundingResult,
                        onSelectTool = { viewModel.selectAITool(it) },
                        onGenerateMusic = { prompt, isFull -> viewModel.generateMusic(prompt, isFull) },
                        onGenerateOrEditImage = { prompt, ratio, bmp -> viewModel.generateOrEditImage(prompt, ratio, bmp) },
                        onGenerateVideoFromText = { prompt, ratio -> viewModel.generateVideoFromText(prompt, ratio) },
                        onAnimateImageToVideo = { prompt, bmp, ratio -> viewModel.animateImageToVideo(prompt, bmp, ratio) },
                        onQueryMaps = { query -> viewModel.queryMapsGrounding(query) },
                        onQuerySearch = { query -> viewModel.querySearchGrounding(query) },
                        onBack = { viewModel.setTab(AppTab.HOME) },
                        onReportAIContent = { aiType, reason, details ->
                            viewModel.reportAIContent(aiType, reason, details)
                        }
                    )
                }
                AppTab.JOBS -> {
                    JobsScreen(
                        jobsList = jobsList,
                        selectedJobType = selectedJobType,
                        district = selectedDistrict,
                        state = selectedState,
                        language = language,
                        onJobTypeSelect = { viewModel.setJobType(it) },
                        onJobClick = { viewModel.selectJob(it) },
                        onReportJobClick = { job ->
                            viewModel.openReportModal("JOB", job.id, job.postName)
                        }
                    )
                }
                AppTab.ROOMS -> {
                    RoomsScreen(
                        rooms = roomsList,
                        savedIds = savedIds,
                        selectedCategory = selectedPropCat,
                        selectedTenantPref = selectedTenantPref,
                        language = language,
                        onCategorySelect = { viewModel.setPropertyCategory(it) },
                        onTenantPrefSelect = { viewModel.setTenantPref(it) },
                        onListingClick = { viewModel.selectListing(it) },
                        onSaveClick = { viewModel.toggleSave(it) },
                        onCallClick = { listing ->
                            Toast.makeText(context, "Calling owner ${listing.sellerName}: ${listing.sellerPhone}", Toast.LENGTH_LONG).show()
                        },
                        onChatClick = { listing ->
                            viewModel.openDirectChatWithSeller(listing)
                        },
                        onOpenReport = { type, id, title ->
                            viewModel.openReportModal(type, id, title)
                        },
                        onPostRoomClick = { viewModel.openCreateListing(ListingDomain.ROOM_RENTAL) }
                    )
                }
                AppTab.MARKETPLACE -> {
                    MarketplaceScreen(
                        items = marketplaceList,
                        savedIds = savedIds,
                        selectedCategory = selectedMarketCat,
                        language = language,
                        onCategorySelect = { viewModel.setMarketCategory(it) },
                        onListingClick = { viewModel.selectListing(it) },
                        onSaveClick = { viewModel.toggleSave(it) },
                        onCallClick = { listing ->
                            Toast.makeText(context, "Calling ${listing.sellerName}: ${listing.sellerPhone}", Toast.LENGTH_LONG).show()
                        },
                        onChatClick = { listing ->
                            viewModel.openDirectChatWithSeller(listing)
                        },
                        onOpenReport = { type, id, title ->
                            viewModel.openReportModal(type, id, title)
                        },
                        onSellItemClick = { viewModel.openCreateListing(ListingDomain.MARKETPLACE) }
                    )
                }
                AppTab.NEWS -> {
                    NewsScreen(
                        newsList = newsList,
                        selectedCategory = selectedNewsCat,
                        selectedScope = selectedNewsScope,
                        state = selectedState,
                        district = selectedDistrict,
                        area = selectedArea,
                        language = language,
                        onCategorySelect = { viewModel.setNewsCategory(it) },
                        onScopeSelect = { viewModel.setNewsScope(it) },
                        onNewsClick = { viewModel.selectNews(it) },
                        onSubmitNewsClick = { viewModel.openSubmitNews() },
                        onRunAutoNews = {
                            viewModel.runAutoNews()
                            Toast.makeText(context, if (language == "hi") "ताज़ा समाचार फेच किए गए!" else "News Ingestion cycle completed!", Toast.LENGTH_SHORT).show()
                        },
                        onOpenReport = { type, id, title ->
                            viewModel.openReportModal(type, id, title)
                        }
                    )
                }
                AppTab.SOCIAL -> {
                    SocialScreen(
                        posts = socialPosts,
                        currentUser = currentUser,
                        language = language,
                        onLikePost = { viewModel.toggleLikePost(it) },
                        onCommentPost = { postId ->
                            viewModel.addComment(postId, "बहुत उपयोगी जानकारी! धन्यवाद।")
                            Toast.makeText(context, "Comment posted", Toast.LENGTH_SHORT).show()
                        },
                        onRepostPost = { postId ->
                            Toast.makeText(context, "Reposted", Toast.LENGTH_SHORT).show()
                        },
                        onSavePost = { id -> viewModel.toggleSave(id) },
                        onCreatePost = { text, hashtags, loc ->
                            viewModel.createSocialPost(text, hashtags, loc)
                            Toast.makeText(context, if (language == "hi") "पोस्ट प्रकाशित हो गई!" else "Post shared!", Toast.LENGTH_SHORT).show()
                        },
                        onAuthorClick = { viewModel.setTab(AppTab.PROFILE) },
                        onReportPost = { postId ->
                            viewModel.reportPost(postId)
                            Toast.makeText(context, if (language == "hi") "पोस्ट की रिपोर्ट दर्ज कर ली गई है।" else "Post reported for review.", Toast.LENGTH_SHORT).show()
                        },
                        onReportUser = { userId, name ->
                            viewModel.reportUser(userId)
                            Toast.makeText(context, if (language == "hi") "यूज़र की रिपोर्ट दर्ज कर ली गई है।" else "User reported for review.", Toast.LENGTH_SHORT).show()
                        },
                        onBlockUser = { userId, name ->
                            viewModel.blockUser(userId, name)
                            Toast.makeText(context, if (language == "hi") "यूज़र को ब्लॉक कर दिया गया है।" else "User has been blocked.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                AppTab.CHAT -> {
                    ChatScreen(
                        conversations = conversations,
                        activeConversationId = activeConversationId,
                        messages = messagesMap,
                        currentUser = currentUser,
                        language = language,
                        onSelectConversation = { viewModel.openChatWith(it) },
                        onCloseActiveChat = { viewModel.closeActiveChat() },
                        onSendMessage = { convId, text -> viewModel.sendMessage(convId, text) },
                        onReportUser = { userId, name ->
                            viewModel.reportUser(userId)
                            Toast.makeText(context, if (language == "hi") "यूज़र की रिपोर्ट दर्ज कर ली गई है।" else "User reported for review.", Toast.LENGTH_SHORT).show()
                        },
                        onBlockUser = { userId, name ->
                            viewModel.blockUser(userId, name)
                            Toast.makeText(context, if (language == "hi") "यूज़र को ब्लॉक कर दिया गया है।" else "User has been blocked.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                AppTab.PROFILE -> {
                    val allListings = roomsList + marketplaceList
                    val myListings = allListings.filter { it.sellerId == currentUser.id }
                    val myPosts = socialPosts.filter { it.authorId == currentUser.id }
                    val savedListings = allListings.filter { savedIds.contains(it.id) }

                    ProfileScreen(
                        user = currentUser,
                        myListings = myListings,
                        myPosts = myPosts,
                        savedListings = savedListings,
                        friendRequests = friendRequests,
                        language = language,
                        onLanguageToggle = { viewModel.toggleLanguage() },
                        onNavigateAdmin = { viewModel.setTab(AppTab.ADMIN) },
                        onListingClick = { viewModel.selectListing(it) },
                        onSaveClick = { viewModel.toggleSave(it) },
                        onCallClick = { listing ->
                            Toast.makeText(context, "Calling ${listing.sellerPhone}", Toast.LENGTH_SHORT).show()
                        },
                        onChatClick = { listing ->
                            viewModel.openDirectChatWithSeller(listing)
                        },
                        onLikePost = { viewModel.toggleLikePost(it) },
                        onCommentPost = { },
                        onRepostPost = { },
                        onAcceptFriendReq = { reqId ->
                            viewModel.acceptFriendRequest(reqId)
                            Toast.makeText(context, if (language == "hi") "मित्र अनुरोध स्वीकार कर लिया गया!" else "Friend request accepted!", Toast.LENGTH_SHORT).show()
                        },
                        onRejectFriendReq = { reqId ->
                            viewModel.rejectFriendRequest(reqId)
                        },
                        onUpdateUser = { updated ->
                            viewModel.updateUser(updated)
                            Toast.makeText(context, if (language == "hi") "प्रोफाइल / खाता अपडेट हुआ!" else "Account updated!", Toast.LENGTH_SHORT).show()
                        },
                        onDeleteAccount = {
                            viewModel.deleteAccount {
                                Toast.makeText(context, if (language == "hi") "आपका खाता और सारा डेटा सफलतापूर्वक हटा दिया गया है।" else "Account and data permanently deleted.", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                }
                AppTab.ADMIN -> {
                    if (com.example.BuildConfig.DEBUG) {
                        val pendingNewsList = newsList.filter { it.verificationStatus == com.example.data.model.NewsVerificationStatus.PENDING_MODERATION || it.verificationStatus == com.example.data.model.NewsVerificationStatus.USER_SUBMITTED }
                        AdminDashboardScreen(
                            stats = adminStats,
                            pendingNews = pendingNewsList,
                            reports = reports,
                            language = language,
                            ingestionState = ingestionState,
                            ingestionLogs = ingestionLogs,
                            onBack = { viewModel.setTab(AppTab.HOME) },
                            onApproveNews = { newsId ->
                                viewModel.approveNews(newsId)
                                Toast.makeText(context, if (language == "hi") "समाचार स्वीकृत और प्रकाशित किया गया!" else "News approved & published!", Toast.LENGTH_SHORT).show()
                            },
                            onRejectNews = { newsId ->
                                viewModel.rejectNews(newsId)
                                Toast.makeText(context, if (language == "hi") "समाचार अस्वीकार किया गया" else "News rejected", Toast.LENGTH_SHORT).show()
                            },
                            onResolveReport = { repId, dismissed ->
                                viewModel.resolveReport(repId, dismissed)
                                Toast.makeText(context, if (dismissed) "Report dismissed" else "Content removed and violation resolved", Toast.LENGTH_SHORT).show()
                            },
                            onRunAutoNews = {
                                viewModel.runAutoNews()
                                Toast.makeText(context, "Automated RSS News Ingestion Pipeline executed successfully!", Toast.LENGTH_LONG).show()
                            },
                            onTriggerIngestion = { source ->
                                viewModel.triggerDataIngestion(source)
                                val msg = if (source != null) "Syncing $source..." else "Running full data ingestion pipeline from roomdekhobgt.com & jobdekhobgt.com..."
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        LaunchedEffect(Unit) {
                            viewModel.setTab(AppTab.HOME)
                        }
                    }
                }
            }
        }
    }

    // Modal Overlays
    if (showLocationSelector) {
        LocationSelectorDialog(
            currentState = selectedState,
            currentDistrict = selectedDistrict,
            currentArea = selectedArea,
            language = language,
            onDismiss = { viewModel.toggleLocationSelector(false) },
            onLocationSelected = { state, dist, area ->
                viewModel.setLocation(state, dist, area)
                Toast.makeText(context, "Location set to $area, $dist, $state", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showPostModal) {
        PostCreationDialog(
            language = language,
            onDismiss = { viewModel.togglePostModal(false) },
            onSelectCreateRoom = { viewModel.openCreateListing(ListingDomain.ROOM_RENTAL) },
            onSelectCreateMarketplace = { viewModel.openCreateListing(ListingDomain.MARKETPLACE) },
            onSelectCreateSocialPost = {
                viewModel.togglePostModal(false)
                viewModel.setTab(AppTab.SOCIAL)
            },
            onSelectSubmitNews = { viewModel.openSubmitNews() }
        )
    }

    if (showSubmitNewsModal) {
        SubmitNewsDialog(
            state = selectedState,
            district = selectedDistrict,
            area = selectedArea,
            language = language,
            onDismiss = { viewModel.closeSubmitNews() },
            onSubmitNews = { headline, summary, content, category, source ->
                viewModel.submitNews(headline, summary, content, category, source)
                Toast.makeText(context, if (language == "hi") "समाचार संपादकीय समीक्षा के लिए भेज दिया गया!" else "News submitted for editorial review!", Toast.LENGTH_LONG).show()
            }
        )
    }

    if (showCreateListingModal != null) {
        val domain = showCreateListingModal!!
        CreateListingDialog(
            domain = domain,
            state = selectedState,
            district = selectedDistrict,
            area = selectedArea,
            language = language,
            currentUser = currentUser,
            onDismiss = { viewModel.closeCreateListing() },
            onSubmitListing = { listing ->
                viewModel.addListing(listing)
                Toast.makeText(context, if (language == "hi") "आपकी लिस्टिंग सफलतापूर्वक प्रकाशित हो गई!" else "Listing published successfully!", Toast.LENGTH_LONG).show()
            }
        )
    }

    if (selectedListing != null) {
        ListingDetailDialog(
            listing = selectedListing!!,
            language = language,
            onDismiss = { viewModel.selectListing(null) },
            onCallClick = {
                Toast.makeText(context, "Calling ${selectedListing!!.sellerPhone}", Toast.LENGTH_LONG).show()
            },
            onChatClick = {
                val listing = selectedListing!!
                viewModel.selectListing(null)
                viewModel.openDirectChatWithSeller(listing)
            },
            onReportClick = {
                val listing = selectedListing!!
                viewModel.selectListing(null)
                viewModel.openReportModal("LISTING", listing.id, listing.title)
            }
        )
    }

    if (selectedNews != null) {
        NewsDetailDialog(
            news = selectedNews!!,
            language = language,
            onDismiss = { viewModel.selectNews(null) },
            onReportClick = {
                val news = selectedNews!!
                viewModel.selectNews(null)
                viewModel.openReportModal("NEWS", news.id, news.hindiHeadline)
            }
        )
    }

    if (selectedJob != null) {
        JobDetailDialog(
            job = selectedJob!!,
            language = language,
            onDismiss = { viewModel.selectJob(null) },
            onReportClick = {
                val job = selectedJob!!
                viewModel.selectJob(null)
                viewModel.openReportModal("JOB", job.id, job.postName)
            }
        )
    }

    if (com.example.BuildConfig.DEBUG && showPipelineSimulator) {
        PipelineSimulatorDialog(
            executions = pipelineRuns,
            language = language,
            onRunPipeline = { url, src, cat ->
                viewModel.runPipeline(url, src, cat)
                Toast.makeText(context, "11-Step Verification Pipeline completed!", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { viewModel.togglePipelineSimulator(false) }
        )
    }

    if (showNotifsModal) {
        NotificationsDialog(
            notifications = notifications,
            language = language,
            onDismiss = { viewModel.toggleNotifications(false) }
        )
    }

    if (showReportModal != null) {
        val target = showReportModal!!
        ReportDialog(
            targetType = target.first,
            targetTitle = target.third,
            language = language,
            onDismiss = { viewModel.closeReportModal() },
            onSubmitReport = { reason, details ->
                viewModel.submitReport(reason, details)
                Toast.makeText(context, if (language == "hi") "रिपोर्ट दर्ज कर ली गई है। हमारी टीम समीक्षा करेगी।" else "Report submitted for moderation review.", Toast.LENGTH_LONG).show()
            }
        )
    }
}
