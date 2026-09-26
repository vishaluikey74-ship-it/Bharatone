package com.example.data.ingestion

import android.util.Log
import com.example.data.local.db.AppDatabase
import com.example.data.local.entity.IngestionLogEntity
import com.example.data.local.entity.toEntity
import com.example.data.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

sealed class IngestionSyncState {
    data class Idle(
        val lastSyncTimestamp: Long? = null,
        val totalPropertiesCount: Int = 0,
        val totalJobsCount: Int = 0
    ) : IngestionSyncState()

    data class Syncing(
        val currentSource: String,
        val progressPercent: Float,
        val statusMessage: String
    ) : IngestionSyncState()

    data class Success(
        val propertiesIngested: Int,
        val jobsIngested: Int,
        val syncTimestamp: Long,
        val summary: String
    ) : IngestionSyncState()

    data class Failed(
        val errorMessage: String,
        val failedTimestamp: Long = System.currentTimeMillis()
    ) : IngestionSyncState()
}

class DataIngestionService(
    private val database: AppDatabase? = null,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    private val _syncState = MutableStateFlow<IngestionSyncState>(IngestionSyncState.Idle())
    val syncState: StateFlow<IngestionSyncState> = _syncState.asStateFlow()

    private val _pibNewsList = MutableStateFlow<List<NewsArticle>>(emptyList())
    val pibNewsList: StateFlow<List<NewsArticle>> = _pibNewsList.asStateFlow()

    private val _newsRefreshError = MutableStateFlow<String?>(null)
    val newsRefreshError: StateFlow<String?> = _newsRefreshError.asStateFlow()

    private var autoSyncJob: Job? = null

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    companion object {
        private const val PIB_ENGLISH_RSS_URL = "https://pib.gov.in/RssMain.aspx?ModId=6&Lang=1&Regid=3"
        private const val PIB_HINDI_RSS_URL = "https://pib.gov.in/RssMain.aspx?ModId=6&Lang=2&Regid=3"
        private const val MAX_NEWS_ITEMS = 50
    }

    init {
        // Start auto ingestion sync loop on startup
        startAutoSyncLoop()
    }

    fun startAutoSyncLoop() {
        autoSyncJob?.cancel()
        autoSyncJob = scope.launch {
            // Initial sync on app startup
            fetchPibNews()
            syncAll(isAuto = true)

            // Periodic sync check every 30 minutes
            while (isActive) {
                delay(30 * 60 * 1000L)
                fetchPibNews()
                syncAll(isAuto = true)
            }
        }
    }

    /**
     * Fetch real live news from official Press Information Bureau (PIB) RSS feeds:
     * - English: https://pib.gov.in/RssMain.aspx?ModId=6&Lang=1&Regid=3
     * - Hindi: https://pib.gov.in/RssMain.aspx?ModId=6&Lang=2&Regid=3
     *
     * Stores ONLY: title, link, pubDate, source name 'PIB (pib.gov.in)'.
     * Summary = empty ("").
     * verificationStatus = SOURCE_SUMMARY, views/likes/shares = 0.
     * Duplicate removal by link. Keeps latest 50 items.
     * Crash safe with robust try/catch.
     */
    suspend fun fetchPibNews(): Boolean = withContext(Dispatchers.IO) {
        try {
            val englishArticles = fetchAndParseRssFeed(PIB_ENGLISH_RSS_URL, isHindi = false)
            val hindiArticles = fetchAndParseRssFeed(PIB_HINDI_RSS_URL, isHindi = true)

            val combined = (hindiArticles + englishArticles)
                .filter { it.hindiHeadline.isNotBlank() || it.englishHeadline.isNotBlank() }
                .distinctBy { it.sourceUrl.ifBlank { it.hindiHeadline + it.englishHeadline } }
                .sortedByDescending { it.publishedAt }
                .take(MAX_NEWS_ITEMS)

            if (combined.isNotEmpty()) {
                _pibNewsList.value = combined
                _newsRefreshError.value = null
                Log.d("DataIngestionService", "Successfully fetched ${combined.size} PIB news items")
                return@withContext true
            } else {
                // If network returned empty or failed, retain previous news if present
                if (_pibNewsList.value.isEmpty()) {
                    _newsRefreshError.value = null // keep clean for empty state
                } else {
                    _newsRefreshError.value = "Could not refresh, check internet"
                }
                return@withContext false
            }
        } catch (e: SocketTimeoutException) {
            Log.w("DataIngestionService", "PIB RSS socket timeout: ${e.message}")
            if (_pibNewsList.value.isNotEmpty()) {
                _newsRefreshError.value = "Could not refresh, check internet"
            }
            return@withContext false
        } catch (e: UnknownHostException) {
            Log.w("DataIngestionService", "PIB RSS host unreachable: ${e.message}")
            if (_pibNewsList.value.isNotEmpty()) {
                _newsRefreshError.value = "Could not refresh, check internet"
            }
            return@withContext false
        } catch (e: IOException) {
            Log.w("DataIngestionService", "PIB RSS IO error: ${e.message}")
            if (_pibNewsList.value.isNotEmpty()) {
                _newsRefreshError.value = "Could not refresh, check internet"
            }
            return@withContext false
        } catch (e: XmlPullParserException) {
            Log.w("DataIngestionService", "PIB RSS XML parse error: ${e.message}")
            if (_pibNewsList.value.isNotEmpty()) {
                _newsRefreshError.value = "Could not refresh, check internet"
            }
            return@withContext false
        } catch (e: IllegalStateException) {
            Log.w("DataIngestionService", "PIB RSS state error: ${e.message}")
            if (_pibNewsList.value.isNotEmpty()) {
                _newsRefreshError.value = "Could not refresh, check internet"
            }
            return@withContext false
        } catch (e: Exception) {
            Log.w("DataIngestionService", "PIB RSS general error: ${e.message}")
            if (_pibNewsList.value.isNotEmpty()) {
                _newsRefreshError.value = "Could not refresh, check internet"
            }
            return@withContext false
        }
    }

    private fun fetchAndParseRssFeed(feedUrl: String, isHindi: Boolean): List<NewsArticle> {
        val resultList = mutableListOf<NewsArticle>()
        try {
            val request = Request.Builder()
                .url(feedUrl)
                .header("User-Agent", "BharatOne-NewsReader/1.0 (Android; Official PIB RSS)")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w("DataIngestionService", "PIB feed HTTP ${response.code} for $feedUrl")
                return emptyList()
            }

            val xmlData = response.body?.string() ?: return emptyList()
            if (xmlData.isBlank()) return emptyList()

            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xmlData))

            var eventType = parser.eventType
            var inItem = false
            var currentTitle: String? = null
            var currentLink: String? = null
            var currentPubDate: String? = null

            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = parser.name?.lowercase(Locale.ROOT)
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (tagName == "item") {
                            inItem = true
                            currentTitle = null
                            currentLink = null
                            currentPubDate = null
                        } else if (inItem) {
                            when (tagName) {
                                "title" -> currentTitle = safeNextText(parser)
                                "link" -> currentLink = safeNextText(parser)
                                "pubdate" -> currentPubDate = safeNextText(parser)
                                "guid" -> {
                                    if (currentLink.isNullOrBlank()) {
                                        currentLink = safeNextText(parser)
                                    }
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (tagName == "item" && inItem) {
                            inItem = false
                            val title = (currentTitle ?: "").trim()
                            val link = (currentLink ?: "").trim()
                            val pubDateStr = (currentPubDate ?: "").trim()

                            if (title.isNotBlank()) {
                                val parsedTimestamp = parseRssPubDateToTimestamp(pubDateStr)
                                val formattedDate = formatPubDateForDisplay(parsedTimestamp, pubDateStr)
                                val articleId = "pib_" + (link.hashCode().toString().replace("-", "n"))

                                val article = NewsArticle(
                                    id = articleId,
                                    hindiHeadline = if (isHindi) title else "",
                                    englishHeadline = if (!isHindi) title else "",
                                    hindiSummary = "",
                                    englishSummary = "",
                                    fullContentHindi = "",
                                    fullContentEnglish = "",
                                    sourceName = "PIB (pib.gov.in)",
                                    sourceUrl = link.ifBlank { "https://pib.gov.in" },
                                    sourceAttribution = SourceAttribution(
                                        sourceName = "PIB (pib.gov.in)",
                                        sourceUrl = link.ifBlank { "https://pib.gov.in" },
                                        isOfficialGovSource = true,
                                        isApprovedLicenseFeed = true,
                                        publicationDateTime = formattedDate,
                                        updatedDateTime = formattedDate,
                                        originalAuthorOrDept = "Press Information Bureau, Govt. of India",
                                        copyrightNotice = "Official PIB press release summary. Content belongs to Press Information Bureau, Govt. of India."
                                    ),
                                    publishedAt = parsedTimestamp,
                                    updatedAt = parsedTimestamp,
                                    state = "Madhya Pradesh",
                                    district = "Balaghat",
                                    city = "Balaghat",
                                    area = "All India",
                                    category = NewsCategory.NATIONAL,
                                    verificationStatus = NewsVerificationStatus.SOURCE_SUMMARY,
                                    publicationLevel = PublicationLevel.LEVEL_1_AUTO,
                                    isBreaking = false,
                                    viewsCount = 0,
                                    likesCount = 0,
                                    sharesCount = 0,
                                    isFactChecked = false
                                )
                                resultList.add(article)
                            }
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.w("DataIngestionService", "Error parsing feed $feedUrl: ${e.message}")
        }
        return resultList
    }

    private fun safeNextText(parser: XmlPullParser): String {
        return try {
            parser.nextText() ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    private fun parseRssPubDateToTimestamp(pubDateStr: String): Long {
        if (pubDateStr.isBlank()) return System.currentTimeMillis()
        val formats = listOf(
            "EEE, dd MMM yyyy HH:mm:ss z",
            "EEE, dd MMM yyyy HH:mm:ss Z",
            "dd MMM yyyy HH:mm:ss z",
            "dd MMM yyyy HH:mm:ss Z",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd HH:mm:ss"
        )
        for (pattern in formats) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                val date = sdf.parse(pubDateStr)
                if (date != null) return date.time
            } catch (e: Exception) {
                // continue to next pattern
            }
        }
        return System.currentTimeMillis()
    }

    private fun formatPubDateForDisplay(timestamp: Long, originalStr: String): String {
        return try {
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            sdf.format(Date(timestamp))
        } catch (e: Exception) {
            if (originalStr.isNotBlank()) originalStr else "Recent"
        }
    }

    suspend fun syncAll(isAuto: Boolean = false): Boolean = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        try {
            _syncState.value = IngestionSyncState.Syncing(
                currentSource = "PIB (pib.gov.in)",
                progressPercent = 0.20f,
                statusMessage = "Connecting to PIB official feeds..."
            )

            // Sync PIB News
            fetchPibNews()

            // 1. Fetch and parse Room Listings from roomdekhobgt.com (empty by default for compliance)
            _syncState.value = IngestionSyncState.Syncing(
                currentSource = "roomdekhobgt.com",
                progressPercent = 0.50f,
                statusMessage = "Checking room portal feeds..."
            )
            val roomListings = fetchFromRoomDekhoBgt()
            val roomEntities = roomListings.map { it.toEntity("roomdekhobgt.com") }
            if (roomEntities.isNotEmpty() && database != null) {
                database.listingDao().insertListings(roomEntities)
            } else if (database != null) {
                database.listingDao().deleteListingsBySource("roomdekhobgt.com")
            }

            // 2. Fetch and parse Jobs from jobdekhobgt.com (empty by default for compliance)
            _syncState.value = IngestionSyncState.Syncing(
                currentSource = "jobdekhobgt.com",
                progressPercent = 0.80f,
                statusMessage = "Checking job feeds..."
            )
            val jobListings = fetchFromJobDekhoBgt()
            val jobEntities = jobListings.map { it.toEntity("jobdekhobgt.com") }
            if (jobEntities.isNotEmpty() && database != null) {
                database.jobDao().insertJobs(jobEntities)
            } else if (database != null) {
                database.jobDao().deleteJobsBySource("jobdekhobgt.com")
            }

            // 3. Log Ingestion Audit
            val duration = System.currentTimeMillis() - startTime
            val totalNewsCount = _pibNewsList.value.size
            val summaryText = "Ingested $totalNewsCount live news items from PIB (pib.gov.in)"

            if (database != null) {
                database.ingestionLogDao().insertLog(
                    IngestionLogEntity(
                        sourceDomain = "pib.gov.in",
                        syncedAt = System.currentTimeMillis(),
                        itemsIngested = totalNewsCount,
                        status = "SUCCESS",
                        summary = summaryText,
                        durationMs = duration
                    )
                )
            }

            _syncState.value = IngestionSyncState.Success(
                propertiesIngested = roomEntities.size,
                jobsIngested = jobEntities.size,
                syncTimestamp = System.currentTimeMillis(),
                summary = summaryText
            )
            return@withContext true
        } catch (e: Exception) {
            _syncState.value = IngestionSyncState.Failed(
                errorMessage = e.localizedMessage ?: "Unknown sync error"
            )
            if (database != null) {
                database.ingestionLogDao().insertLog(
                    IngestionLogEntity(
                        sourceDomain = "all",
                        syncedAt = System.currentTimeMillis(),
                        itemsIngested = 0,
                        status = "FAILED",
                        summary = "Sync error: ${e.localizedMessage}",
                        durationMs = System.currentTimeMillis() - startTime
                    )
                )
            }
            return@withContext false
        }
    }

    suspend fun syncSource(sourceDomain: String): Boolean = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        try {
            _syncState.value = IngestionSyncState.Syncing(
                currentSource = sourceDomain,
                progressPercent = 0.3f,
                statusMessage = "Syncing live feeds from $sourceDomain..."
            )

            if (sourceDomain.contains("pib", ignoreCase = true)) {
                fetchPibNews()
            }

            val duration = System.currentTimeMillis() - startTime
            val summary = "Successfully synced $sourceDomain"

            if (database != null) {
                database.ingestionLogDao().insertLog(
                    IngestionLogEntity(
                        sourceDomain = sourceDomain,
                        syncedAt = System.currentTimeMillis(),
                        itemsIngested = _pibNewsList.value.size,
                        status = "SUCCESS",
                        summary = summary,
                        durationMs = duration
                    )
                )
            }

            _syncState.value = IngestionSyncState.Success(
                propertiesIngested = 0,
                jobsIngested = 0,
                syncTimestamp = System.currentTimeMillis(),
                summary = summary
            )
            return@withContext true
        } catch (e: Exception) {
            _syncState.value = IngestionSyncState.Failed(
                errorMessage = e.localizedMessage ?: "Failed to sync $sourceDomain"
            )
            return@withContext false
        }
    }

    private fun fetchFromRoomDekhoBgt(): List<Listing> = emptyList()

    private fun fetchFromJobDekhoBgt(): List<JobListing> = emptyList()
}
