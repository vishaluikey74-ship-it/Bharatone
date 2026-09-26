package com.example.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiApiService
import com.example.data.model.*
import com.example.data.repository.BharatRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppTab {
    HOME,
    ROOMS,
    MARKETPLACE,
    NEWS,
    JOBS,
    SOCIAL,
    CHAT,
    PROFILE,
    ADMIN,
    AI_STUDIO
}

class BharatViewModel(
    private val repository: BharatRepository = BharatRepository.getInstance(),
    private val geminiApiService: GeminiApiService = GeminiApiService()
) : ViewModel() {

    // Active Navigation Tab
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // AI Studio State Flows
    private val _selectedAITool = MutableStateFlow(AIStudioTool.MUSIC_GEN)
    val selectedAITool: StateFlow<AIStudioTool> = _selectedAITool.asStateFlow()

    private val _isAILoading = MutableStateFlow(false)
    val isAILoading: StateFlow<Boolean> = _isAILoading.asStateFlow()

    private val _aiStatusMessage = MutableStateFlow("")
    val aiStatusMessage: StateFlow<String> = _aiStatusMessage.asStateFlow()

    private val _lastGeneratedMusic = MutableStateFlow<GeneratedMusicResult?>(null)
    val lastGeneratedMusic: StateFlow<GeneratedMusicResult?> = _lastGeneratedMusic.asStateFlow()

    private val _lastGeneratedImage = MutableStateFlow<GeneratedImageResult?>(null)
    val lastGeneratedImage: StateFlow<GeneratedImageResult?> = _lastGeneratedImage.asStateFlow()

    private val _lastGeneratedVideo = MutableStateFlow<GeneratedVideoResult?>(null)
    val lastGeneratedVideo: StateFlow<GeneratedVideoResult?> = _lastGeneratedVideo.asStateFlow()

    private val _mapsGroundingResult = MutableStateFlow<Pair<String, List<GroundedPlaceItem>>?>(null)
    val mapsGroundingResult: StateFlow<Pair<String, List<GroundedPlaceItem>>?> = _mapsGroundingResult.asStateFlow()

    private val _searchGroundingResult = MutableStateFlow<Pair<String, List<GroundedWebSource>>?>(null)
    val searchGroundingResult: StateFlow<Pair<String, List<GroundedWebSource>>?> = _searchGroundingResult.asStateFlow()

    // Refresh state
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Ingestion Flows
    val ingestionState = repository.ingestionState
    val ingestionLogs = repository.ingestionLogs

    // Repository Flows
    val currentUser = repository.currentUser
    val selectedState = repository.selectedState
    val selectedDistrict = repository.selectedDistrict
    val selectedArea = repository.selectedArea
    val appLanguage = repository.appLanguage
    val listings = repository.listings
    val newsList = repository.newsList
    val lastNewsSyncTime = repository.lastNewsSyncTime
    val jobsList = repository.jobsList
    val approvedSources = repository.approvedSources
    val pipelineExecutions = repository.pipelineExecutions
    val socialPosts = repository.socialPosts
    val conversations = repository.conversations
    val messages = repository.messages
    val friendRequests = repository.friendRequests
    val friendsList = repository.friendsList
    val followingList = repository.followingList
    val notifications = repository.notifications
    val reports = repository.reports
    val blockedUserIds = repository.blockedUserIds
    val savedListingIds = repository.savedListingIds
    val adminStats = repository.adminStats

    // Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Room Screen Filters
    private val _selectedPropertyCategory = MutableStateFlow<PropertyCategory?>(null)
    val selectedPropertyCategory: StateFlow<PropertyCategory?> = _selectedPropertyCategory.asStateFlow()

    private val _selectedTenantPref = MutableStateFlow("All")
    val selectedTenantPref: StateFlow<String> = _selectedTenantPref.asStateFlow()

    // Marketplace Screen Filters
    private val _selectedMarketCategory = MutableStateFlow<MarketCategory?>(null)
    val selectedMarketCategory: StateFlow<MarketCategory?> = _selectedMarketCategory.asStateFlow()

    // News Screen Filters
    private val _selectedNewsCategory = MutableStateFlow<NewsCategory?>(null)
    val selectedNewsCategory: StateFlow<NewsCategory?> = _selectedNewsCategory.asStateFlow()

    private val _selectedNewsScope = MutableStateFlow("ALL") // "MY_AREA", "MY_DISTRICT", "MY_STATE", "ALL"
    val selectedNewsScope: StateFlow<String> = _selectedNewsScope.asStateFlow()

    private val _selectedPublicationLevel = MutableStateFlow<PublicationLevel?>(null)
    val selectedPublicationLevel: StateFlow<PublicationLevel?> = _selectedPublicationLevel.asStateFlow()

    // Job Screen Filters
    private val _selectedJobType = MutableStateFlow<JobType?>(null) // null = ALL, GOVERNMENT, PRIVATE
    val selectedJobType: StateFlow<JobType?> = _selectedJobType.asStateFlow()

    // Active Selected Item for Details Dialog
    private val _selectedListing = MutableStateFlow<Listing?>(null)
    val selectedListing: StateFlow<Listing?> = _selectedListing.asStateFlow()

    private val _selectedNews = MutableStateFlow<NewsArticle?>(null)
    val selectedNews: StateFlow<NewsArticle?> = _selectedNews.asStateFlow()

    private val _selectedJob = MutableStateFlow<JobListing?>(null)
    val selectedJob: StateFlow<JobListing?> = _selectedJob.asStateFlow()

    // Active Chat Conversation
    private val _activeConversationId = MutableStateFlow<String?>(null)
    val activeConversationId: StateFlow<String?> = _activeConversationId.asStateFlow()

    // Dialog & Sheet Controls
    private val _showLocationSelector = MutableStateFlow(false)
    val showLocationSelector: StateFlow<Boolean> = _showLocationSelector.asStateFlow()

    private val _showPostModal = MutableStateFlow(false)
    val showPostModal: StateFlow<Boolean> = _showPostModal.asStateFlow()

    private val _showSubmitNewsModal = MutableStateFlow(false)
    val showSubmitNewsModal: StateFlow<Boolean> = _showSubmitNewsModal.asStateFlow()

    private val _showCreateListingModal = MutableStateFlow<ListingDomain?>(null)
    val showCreateListingModal: StateFlow<ListingDomain?> = _showCreateListingModal.asStateFlow()

    private val _showNotificationsModal = MutableStateFlow(false)
    val showNotificationsModal: StateFlow<Boolean> = _showNotificationsModal.asStateFlow()

    private val _showReportModal = MutableStateFlow<Triple<String, String, String>?>(null) // (type, id, title)
    val showReportModal: StateFlow<Triple<String, String, String>?> = _showReportModal.asStateFlow()

    private val _showFriendRequestsModal = MutableStateFlow(false)
    val showFriendRequestsModal: StateFlow<Boolean> = _showFriendRequestsModal.asStateFlow()

    private val _showPipelineSimulatorModal = MutableStateFlow(false)
    val showPipelineSimulatorModal: StateFlow<Boolean> = _showPipelineSimulatorModal.asStateFlow()

    // Filtered Listings for Room Screen
    val filteredRooms: StateFlow<List<Listing>> = combine(
        listings,
        selectedDistrict,
        selectedPropertyCategory,
        selectedTenantPref,
        searchQuery
    ) { allListings, district, propCat, tenantPref, query ->
        allListings.filter { listing ->
            listing.domain == ListingDomain.ROOM_RENTAL &&
            (propCat == null || listing.propertyCategory == propCat) &&
            (tenantPref == "All" || listing.tenantPreference.equals(tenantPref, ignoreCase = true) || listing.tenantPreference == "Anyone") &&
            (query.isBlank() || listing.title.contains(query, ignoreCase = true) || listing.description.contains(query, ignoreCase = true) || listing.area.contains(query, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Listings for Marketplace Screen
    val filteredMarketplace: StateFlow<List<Listing>> = combine(
        listings,
        selectedMarketCategory,
        searchQuery
    ) { allListings, marketCat, query ->
        allListings.filter { listing ->
            listing.domain == ListingDomain.MARKETPLACE &&
            (marketCat == null || listing.marketCategory == marketCat) &&
            (query.isBlank() || listing.title.contains(query, ignoreCase = true) || listing.description.contains(query, ignoreCase = true) || listing.brand.contains(query, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered News with Policy and Level verification
    val filteredNews: StateFlow<List<NewsArticle>> = combine(
        listOf(
            newsList,
            selectedNewsCategory,
            selectedNewsScope,
            selectedDistrict,
            selectedState,
            searchQuery,
            selectedPublicationLevel
        )
    ) { args ->
        @Suppress("UNCHECKED_CAST")
        val allNews = args[0] as List<NewsArticle>
        val category = args[1] as NewsCategory?
        val scope = args[2] as String
        val district = args[3] as String
        val state = args[4] as String
        val query = args[5] as String
        val level = args[6] as PublicationLevel?

        allNews.filter { article ->
            val matchesCategory = category == null || article.category == category
            val matchesLevel = level == null || article.publicationLevel == level
            val matchesScope = when (scope) {
                "MY_AREA" -> article.district.equals(district, ignoreCase = true)
                "MY_DISTRICT" -> article.district.equals(district, ignoreCase = true)
                "MY_STATE" -> article.state.equals(state, ignoreCase = true) || article.state == "All India"
                else -> true
            }
            val matchesQuery = query.isBlank() ||
                    article.hindiHeadline.contains(query, ignoreCase = true) ||
                    article.hindiSummary.contains(query, ignoreCase = true) ||
                    article.englishHeadline.contains(query, ignoreCase = true) ||
                    article.sourceName.contains(query, ignoreCase = true)

            matchesCategory && matchesLevel && matchesScope && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Jobs
    val filteredJobs: StateFlow<List<JobListing>> = combine(
        jobsList,
        selectedJobType,
        selectedDistrict,
        searchQuery
    ) { allJobs, jobType, district, query ->
        allJobs.filter { job ->
            val matchesType = jobType == null || job.jobType == jobType
            val matchesQuery = query.isBlank() ||
                    job.postName.contains(query, ignoreCase = true) ||
                    job.hindiPostName.contains(query, ignoreCase = true) ||
                    job.organization.contains(query, ignoreCase = true) ||
                    job.qualification.contains(query, ignoreCase = true) ||
                    job.location.contains(query, ignoreCase = true)

            matchesType && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ================= AI STUDIO ACTIONS =================

    fun selectAITool(tool: AIStudioTool) {
        _selectedAITool.value = tool
    }

    // 1. Generate Music (lyria-3-clip-preview / lyria-3-pro-preview)
    fun generateMusic(prompt: String, isFullLength: Boolean = false) {
        viewModelScope.launch {
            _isAILoading.value = true
            _aiStatusMessage.value = if (isFullLength) "Generating full track with lyria-3-pro-preview..." else "Generating clip with lyria-3-clip-preview..."
            val result = geminiApiService.generateMusic(prompt, isFullLength)
            result.onSuccess { music ->
                _lastGeneratedMusic.value = music
            }
            _isAILoading.value = false
            _aiStatusMessage.value = ""
        }
    }

    // 2. Create & Edit Images (gemini-3.1-flash-image-preview)
    fun generateOrEditImage(prompt: String, aspectRatio: String = "1:1", inputBitmap: Bitmap? = null) {
        viewModelScope.launch {
            _isAILoading.value = true
            _aiStatusMessage.value = if (inputBitmap != null) "Editing image with gemini-3.1-flash-image-preview..." else "Generating 1K image with gemini-3.1-flash-image-preview..."
            val result = geminiApiService.generateOrEditImage(prompt, aspectRatio, inputBitmap)
            result.onSuccess { img ->
                _lastGeneratedImage.value = img
            }
            _isAILoading.value = false
            _aiStatusMessage.value = ""
        }
    }

    // 3. Generate Video from Text (veo-3.1-fast-generate-preview)
    fun generateVideoFromText(prompt: String, aspectRatio: String = "16:9") {
        viewModelScope.launch {
            _isAILoading.value = true
            _aiStatusMessage.value = "Generating Veo 3 video ($aspectRatio) with veo-3.1-fast-generate-preview..."
            val result = geminiApiService.generateVideoFromText(prompt, aspectRatio)
            result.onSuccess { video ->
                _lastGeneratedVideo.value = video
            }
            _isAILoading.value = false
            _aiStatusMessage.value = ""
        }
    }

    // 4. Animate Images into Video (veo-3.1-fast-generate-preview)
    fun animateImageToVideo(prompt: String, inputBitmap: Bitmap, aspectRatio: String = "16:9") {
        viewModelScope.launch {
            _isAILoading.value = true
            _aiStatusMessage.value = "Animating photo into video with veo-3.1-fast-generate-preview..."
            val result = geminiApiService.animateImageToVideo(prompt, inputBitmap, aspectRatio)
            result.onSuccess { video ->
                _lastGeneratedVideo.value = video
            }
            _isAILoading.value = false
            _aiStatusMessage.value = ""
        }
    }

    // 5. Google Maps Grounding (gemini-3.5-flash with googleMaps tool)
    fun queryMapsGrounding(prompt: String) {
        viewModelScope.launch {
            _isAILoading.value = true
            val city = "${selectedDistrict.value}, ${selectedState.value}"
            _aiStatusMessage.value = "Querying Google Maps grounded places in $city with gemini-3.5-flash..."
            val result = geminiApiService.queryMapsGrounding(prompt, city)
            result.onSuccess { pair ->
                _mapsGroundingResult.value = pair
            }
            _isAILoading.value = false
            _aiStatusMessage.value = ""
        }
    }

    // 6. Google Search Grounding (gemini-3.5-flash with googleSearch tool)
    fun querySearchGrounding(prompt: String) {
        viewModelScope.launch {
            _isAILoading.value = true
            _aiStatusMessage.value = "Fact-checking with Google Search Grounding & gemini-3.5-flash..."
            val result = geminiApiService.querySearchGrounding(prompt)
            result.onSuccess { pair ->
                _searchGroundingResult.value = pair
            }
            _isAILoading.value = false
            _aiStatusMessage.value = ""
        }
    }

    // ================= ACTIONS =================

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setPropertyCategory(category: PropertyCategory?) {
        _selectedPropertyCategory.value = category
    }

    fun setTenantPref(pref: String) {
        _selectedTenantPref.value = pref
    }

    fun setMarketCategory(category: MarketCategory?) {
        _selectedMarketCategory.value = category
    }

    fun setNewsCategory(category: NewsCategory?) {
        _selectedNewsCategory.value = category
    }

    fun setNewsScope(scope: String) {
        _selectedNewsScope.value = scope
    }

    fun selectListing(listing: Listing?) {
        _selectedListing.value = listing
    }

    fun selectNews(news: NewsArticle?) {
        _selectedNews.value = news
    }

    fun openChatWith(conversationId: String) {
        _activeConversationId.value = conversationId
        _currentTab.value = AppTab.CHAT
    }

    fun openDirectChatWithSeller(listing: Listing) {
        val convId = "conv_${listing.sellerId}"
        repository.sendMessage(convId, "नमस्ते ${listing.sellerName} जी, मुझे आपकी '${listing.title}' लिस्टिंग में रुचि है।", listing.title)
        _activeConversationId.value = convId
        _currentTab.value = AppTab.CHAT
    }

    fun closeActiveChat() {
        _activeConversationId.value = null
    }

    fun toggleLocationSelector(show: Boolean) {
        _showLocationSelector.value = show
    }

    fun setLocation(state: String, district: String, area: String) {
        repository.setLocation(state, district, area)
        _showLocationSelector.value = false
    }

    fun toggleLanguage() {
        val nextLang = if (appLanguage.value == "hi") "en" else "hi"
        repository.setLanguage(nextLang)
    }

    fun togglePostModal(show: Boolean) {
        _showPostModal.value = show
    }

    fun openSubmitNews() {
        _showPostModal.value = false
        _showSubmitNewsModal.value = true
    }

    fun closeSubmitNews() {
        _showSubmitNewsModal.value = false
    }

    fun openCreateListing(domain: ListingDomain) {
        _showPostModal.value = false
        _showCreateListingModal.value = domain
    }

    fun closeCreateListing() {
        _showCreateListingModal.value = null
    }

    fun toggleNotifications(show: Boolean) {
        _showNotificationsModal.value = show
    }

    fun toggleFriendRequests(show: Boolean) {
        _showFriendRequestsModal.value = show
    }

    fun setNewsPublicationLevel(level: PublicationLevel?) {
        _selectedPublicationLevel.value = level
    }

    fun setJobType(type: JobType?) {
        _selectedJobType.value = type
    }

    fun selectJob(job: JobListing?) {
        _selectedJob.value = job
    }

    fun togglePipelineSimulator(show: Boolean) {
        _showPipelineSimulatorModal.value = show
    }

    fun runPipeline(inputUrlOrText: String, sourceName: String, category: String = "Govt Recruitment / News") {
        repository.runAIContentVerificationPipeline(inputUrlOrText, sourceName, category)
    }

    fun submitJobListing(job: JobListing) {
        repository.submitJobListing(job)
    }

    fun reportJob(jobId: String) {
        repository.toggleReportJob(jobId)
    }

    fun approveNewsWithStatus(id: String, targetStatus: NewsVerificationStatus = NewsVerificationStatus.EDITOR_REVIEWED) {
        repository.approveNews(id, targetStatus)
    }

    fun openReportModal(targetType: String, targetId: String, targetTitle: String) {
        _showReportModal.value = Triple(targetType, targetId, targetTitle)
    }

    fun closeReportModal() {
        _showReportModal.value = null
    }

    // Proxy repository mutations
    fun toggleSave(id: String) = repository.toggleSave(id)
    fun toggleLikePost(postId: String) = repository.toggleLikePost(postId)
    fun addComment(postId: String, text: String) = repository.addComment(postId, text)
    fun createSocialPost(text: String, hashtags: List<String>, location: String) = repository.createPost(text, hashtags, location)
    fun sendMessage(conversationId: String, text: String) = repository.sendMessage(conversationId, text)
    fun acceptFriendRequest(requestId: String) = repository.acceptFriendRequest(requestId)
    fun rejectFriendRequest(requestId: String) = repository.rejectFriendRequest(requestId)
    fun toggleFollowUser(user: User) = repository.toggleFollowUser(user)
    fun runAutoNews() = repository.runAutoNewsIngestion()
    fun refreshLiveNews() = repository.refreshLiveNews()
    fun approveNews(id: String) = repository.approveNews(id)
    fun rejectNews(id: String) = repository.rejectNews(id)
    fun submitReport(reason: ReportReason, details: String) {
        val target = _showReportModal.value ?: return
        repository.submitReport(target.first, target.second, target.third, reason, details)
        _showReportModal.value = null
    }
    fun resolveReport(reportId: String, dismissed: Boolean = false) = repository.resolveReport(reportId, dismissed)

    fun submitNews(headline: String, summary: String, content: String, category: NewsCategory, source: String) {
        repository.submitUserNews(
            headline = headline,
            summary = summary,
            content = content,
            category = category,
            state = selectedState.value,
            district = selectedDistrict.value,
            area = if (selectedArea.value == "All Areas") "Main Area" else selectedArea.value,
            source = source
        )
        _showSubmitNewsModal.value = false
    }

    fun updateUser(user: User) = repository.updateUser(user)

    fun deleteAccount(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteUserAccount()
            onComplete()
        }
    }

    fun blockUser(userId: String, userName: String = "") {
        repository.blockUser(userId, userName)
    }

    fun reportUser(userId: String, reason: ReportReason = ReportReason.HARASSMENT, details: String = "Inappropriate user behavior") {
        repository.reportUser(userId, reason, details)
    }

    fun reportPost(postId: String, reason: ReportReason = ReportReason.OTHER, details: String = "Inappropriate post content") {
        repository.reportPost(postId, reason, details)
    }

    fun reportAIContent(aiType: String, reason: String, details: String) {
        repository.reportAIContent(aiType, reason, details)
    }

    fun addListing(listing: Listing) {
        repository.addListing(listing)
        _showCreateListingModal.value = null
    }

    fun triggerDataIngestion(sourceDomain: String? = null) {
        repository.triggerDataIngestion(sourceDomain)
    }

    fun refreshHomeContent() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.refreshLiveNews()
            repository.runAutoNewsIngestion()
            repository.triggerDataIngestion()
            kotlinx.coroutines.delay(1000)
            _isRefreshing.value = false
        }
    }
}
