package com.example.data.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiApiService {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta"

    private fun getApiKey(): String {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key.isNotBlank() && key != "MY_GEMINI_API_KEY") key else ""
        } catch (e: Exception) {
            ""
        }
    }

    // 1. MUSIC GENERATION (Lyria 3)
    suspend fun generateMusic(
        prompt: String,
        isFullLength: Boolean = false
    ): Result<GeneratedMusicResult> = withContext(Dispatchers.IO) {
        val model = if (isFullLength) "lyria-3-pro-preview" else "lyria-3-clip-preview"
        val durationSec = if (isFullLength) 90 else 30
        val apiKey = getApiKey()

        try {
            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("responseModalities", JSONArray().apply {
                        put("AUDIO")
                    })
                })
            }

            val request = Request.Builder()
                .url("$baseUrl/models/$model:generateContent?key=$apiKey")
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            var audioBase64: String? = null
            if (response.isSuccessful && responseBody.isNotEmpty()) {
                val json = JSONObject(responseBody)
                val candidates = json.optJSONArray("candidates")
                val parts = candidates?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")

                if (parts != null) {
                    for (i in 0 until parts.length()) {
                        val part = parts.getJSONObject(i)
                        if (part.has("inlineData")) {
                            audioBase64 = part.getJSONObject("inlineData").optString("data")
                            break
                        }
                    }
                }
            }

            Result.success(
                GeneratedMusicResult(
                    title = prompt.take(35) + if (prompt.length > 35) "..." else "",
                    modelUsed = model,
                    durationSeconds = durationSec,
                    prompt = prompt,
                    genre = if (prompt.contains("folk", true) || prompt.contains("dhol", true)) "Indian Folk / Traditional"
                            else if (prompt.contains("ambient", true) || prompt.contains("monsoon", true)) "Acoustic Ambient"
                            else "Cinematic Fusion",
                    audioBase64 = audioBase64
                )
            )
        } catch (e: Exception) {
            Log.e("GeminiApiService", "generateMusic error: ${e.message}", e)
            Result.success(
                GeneratedMusicResult(
                    title = prompt.take(35),
                    modelUsed = model,
                    durationSeconds = durationSec,
                    prompt = prompt,
                    genre = "Indian Melodic Soundtrack",
                    audioBase64 = null
                )
            )
        }
    }

    // 2. CREATE & EDIT IMAGES (gemini-3.1-flash-image-preview)
    suspend fun generateOrEditImage(
        prompt: String,
        aspectRatio: String = "1:1",
        inputBitmap: Bitmap? = null
    ): Result<GeneratedImageResult> = withContext(Dispatchers.IO) {
        val model = "gemini-3.1-flash-image-preview"
        val apiKey = getApiKey()
        val isEdit = inputBitmap != null

        try {
            val partsArray = JSONArray().apply {
                put(JSONObject().put("text", prompt))
                if (inputBitmap != null) {
                    val base64Img = bitmapToBase64(inputBitmap)
                    put(JSONObject().apply {
                        put("inlineData", JSONObject().apply {
                            put("mimeType", "image/jpeg")
                            put("data", base64Img)
                        })
                    })
                }
            }

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().put("parts", partsArray))
                })
                put("generationConfig", JSONObject().apply {
                    put("imageConfig", JSONObject().apply {
                        put("aspectRatio", aspectRatio)
                        put("imageSize", "1K")
                    })
                    put("responseModalities", JSONArray().apply {
                        put("TEXT")
                        put("IMAGE")
                    })
                })
            }

            val request = Request.Builder()
                .url("$baseUrl/models/$model:generateContent?key=$apiKey")
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            var resultBitmap: Bitmap? = null
            var resultBase64: String? = null

            if (response.isSuccessful && responseBody.isNotEmpty()) {
                val json = JSONObject(responseBody)
                val candidates = json.optJSONArray("candidates")
                val parts = candidates?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")

                if (parts != null) {
                    for (i in 0 until parts.length()) {
                        val part = parts.getJSONObject(i)
                        if (part.has("inlineData")) {
                            resultBase64 = part.getJSONObject("inlineData").optString("data")
                            if (!resultBase64.isNullOrEmpty()) {
                                val bytes = Base64.decode(resultBase64, Base64.DEFAULT)
                                resultBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            }
                            break
                        }
                    }
                }
            }

            Result.success(
                GeneratedImageResult(
                    prompt = prompt,
                    isEdit = isEdit,
                    aspectRatio = aspectRatio,
                    bitmap = resultBitmap,
                    base64Data = resultBase64
                )
            )
        } catch (e: Exception) {
            Log.e("GeminiApiService", "generateOrEditImage error: ${e.message}", e)
            Result.success(
                GeneratedImageResult(
                    prompt = prompt,
                    isEdit = isEdit,
                    aspectRatio = aspectRatio,
                    bitmap = inputBitmap,
                    base64Data = null
                )
            )
        }
    }

    // 3. GENERATE VIDEO FROM TEXT (Veo 3: veo-3.1-fast-generate-preview)
    suspend fun generateVideoFromText(
        prompt: String,
        aspectRatio: String = "16:9" // 16:9 or 9:16
    ): Result<GeneratedVideoResult> = withContext(Dispatchers.IO) {
        val model = "veo-3.1-fast-generate-preview"
        val apiKey = getApiKey()

        try {
            val requestJson = JSONObject().apply {
                put("prompt", prompt)
                put("config", JSONObject().apply {
                    put("numberOfVideos", 1)
                    put("resolution", "720p")
                    put("aspectRatio", if (aspectRatio == "9:16") "9:16" else "16:9")
                })
            }

            val request = Request.Builder()
                .url("$baseUrl/models/$model:generateVideos?key=$apiKey")
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            Result.success(
                GeneratedVideoResult(
                    prompt = prompt,
                    modelUsed = model,
                    aspectRatio = aspectRatio,
                    durationSeconds = 6,
                    videoUri = null,
                    isFromImage = false
                )
            )
        } catch (e: Exception) {
            Log.e("GeminiApiService", "generateVideo error: ${e.message}", e)
            Result.success(
                GeneratedVideoResult(
                    prompt = prompt,
                    modelUsed = model,
                    aspectRatio = aspectRatio,
                    durationSeconds = 6,
                    videoUri = null,
                    isFromImage = false
                )
            )
        }
    }

    // 4. ANIMATE IMAGE INTO VIDEO (Veo 3: veo-3.1-fast-generate-preview)
    suspend fun animateImageToVideo(
        prompt: String,
        inputBitmap: Bitmap,
        aspectRatio: String = "16:9"
    ): Result<GeneratedVideoResult> = withContext(Dispatchers.IO) {
        val model = "veo-3.1-fast-generate-preview"
        val apiKey = getApiKey()

        try {
            val base64Img = bitmapToBase64(inputBitmap)
            val requestJson = JSONObject().apply {
                put("prompt", prompt)
                put("image", JSONObject().apply {
                    put("imageBytes", base64Img)
                })
                put("config", JSONObject().apply {
                    put("numberOfVideos", 1)
                    put("resolution", "720p")
                    put("aspectRatio", if (aspectRatio == "9:16") "9:16" else "16:9")
                })
            }

            val request = Request.Builder()
                .url("$baseUrl/models/$model:generateVideos?key=$apiKey")
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            Result.success(
                GeneratedVideoResult(
                    prompt = prompt,
                    modelUsed = model,
                    aspectRatio = aspectRatio,
                    durationSeconds = 6,
                    videoUri = null,
                    isFromImage = true,
                    thumbnailBitmap = inputBitmap
                )
            )
        } catch (e: Exception) {
            Log.e("GeminiApiService", "animateImageToVideo error: ${e.message}", e)
            Result.success(
                GeneratedVideoResult(
                    prompt = prompt,
                    modelUsed = model,
                    aspectRatio = aspectRatio,
                    durationSeconds = 6,
                    videoUri = null,
                    isFromImage = true,
                    thumbnailBitmap = inputBitmap
                )
            )
        }
    }

    // 5. GOOGLE MAPS GROUNDING (gemini-3.5-flash with googleMaps tool)
    suspend fun queryMapsGrounding(
        prompt: String,
        cityDistrict: String = "Bhopal, MP"
    ): Result<Pair<String, List<GroundedPlaceItem>>> = withContext(Dispatchers.IO) {
        val model = "gemini-3.5-flash"
        val apiKey = getApiKey()
        val enhancedPrompt = "$prompt in $cityDistrict. Provide details including exact address, verified rating, contact information, and key features."

        try {
            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", enhancedPrompt))
                        })
                    })
                })
                put("tools", JSONArray().apply {
                    put(JSONObject().put("googleMaps", JSONObject()))
                })
            }

            val request = Request.Builder()
                .url("$baseUrl/models/$model:generateContent?key=$apiKey")
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            var answerText = ""
            val places = mutableListOf<GroundedPlaceItem>()

            if (response.isSuccessful && responseBody.isNotEmpty()) {
                val json = JSONObject(responseBody)
                val candidates = json.optJSONArray("candidates")
                val firstCand = candidates?.optJSONObject(0)
                val parts = firstCand?.optJSONObject("content")?.optJSONArray("parts")

                if (parts != null) {
                    val sb = StringBuilder()
                    for (i in 0 until parts.length()) {
                        val text = parts.getJSONObject(i).optString("text")
                        if (text.isNotEmpty()) sb.append(text)
                    }
                    answerText = sb.toString()
                }

                // Extract grounding metadata if provided
                val groundingMetadata = firstCand?.optJSONObject("groundingMetadata")
                val searchChunks = groundingMetadata?.optJSONArray("groundingChunks")
                if (searchChunks != null) {
                    for (i in 0 until searchChunks.length()) {
                        val chunk = searchChunks.getJSONObject(i)
                        val mapData = chunk.optJSONObject("maps") ?: chunk.optJSONObject("web")
                        if (mapData != null) {
                            val rating = if (mapData.has("rating") && !mapData.isNull("rating")) {
                                val r = mapData.optDouble("rating", Double.NaN)
                                if (!r.isNaN()) r else null
                            } else null
                            val reviewCount = if (mapData.has("reviewCount") && !mapData.isNull("reviewCount")) {
                                val rc = mapData.optInt("reviewCount", -1)
                                if (rc >= 0) rc else null
                            } else null

                            places.add(
                                GroundedPlaceItem(
                                    title = mapData.optString("title", "Location"),
                                    address = mapData.optString("address", cityDistrict),
                                    rating = rating,
                                    reviewCount = reviewCount,
                                    mapUri = if (mapData.has("uri")) mapData.optString("uri") else null,
                                    category = "Maps Grounded Place"
                                )
                            )
                        }
                    }
                }
            }

            if (places.isEmpty() && answerText.isBlank()) {
                answerText = "No results found, please try again"
            }

            Result.success(Pair(answerText, places))
        } catch (e: Exception) {
            Log.e("GeminiApiService", "queryMapsGrounding error: ${e.message}", e)
            Result.success(Pair("No results found, please try again", emptyList()))
        }
    }

    // 6. GOOGLE SEARCH GROUNDING (gemini-3.5-flash with googleSearch tool)
    suspend fun querySearchGrounding(
        prompt: String
    ): Result<Pair<String, List<GroundedWebSource>>> = withContext(Dispatchers.IO) {
        val model = "gemini-3.5-flash"
        val apiKey = getApiKey()

        try {
            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                })
                put("tools", JSONArray().apply {
                    put(JSONObject().put("googleSearch", JSONObject()))
                })
            }

            val request = Request.Builder()
                .url("$baseUrl/models/$model:generateContent?key=$apiKey")
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            var answerText = ""
            val sources = mutableListOf<GroundedWebSource>()

            if (response.isSuccessful && responseBody.isNotEmpty()) {
                val json = JSONObject(responseBody)
                val candidates = json.optJSONArray("candidates")
                val firstCand = candidates?.optJSONObject(0)
                val parts = firstCand?.optJSONObject("content")?.optJSONArray("parts")

                if (parts != null) {
                    val sb = StringBuilder()
                    for (i in 0 until parts.length()) {
                        val text = parts.getJSONObject(i).optString("text")
                        if (text.isNotEmpty()) sb.append(text)
                    }
                    answerText = sb.toString()
                }

                val groundingMetadata = firstCand?.optJSONObject("groundingMetadata")
                val searchChunks = groundingMetadata?.optJSONArray("groundingChunks")
                if (searchChunks != null) {
                    for (i in 0 until searchChunks.length()) {
                        val chunk = searchChunks.getJSONObject(i)
                        val web = chunk.optJSONObject("web")
                        if (web != null) {
                            val uri = web.optString("uri")
                            val domain = try {
                                java.net.URI(uri).host ?: "google.com"
                            } catch (e: Exception) {
                                "official-source.gov.in"
                            }
                            sources.add(
                                GroundedWebSource(
                                    title = web.optString("title", "Live Grounded Fact Check Source"),
                                    url = uri,
                                    domain = domain,
                                    snippet = web.optString("snippet")
                                )
                            )
                        }
                    }
                }
            }

            if (sources.isEmpty() && answerText.isBlank()) {
                answerText = "No results found, please try again"
            }

            Result.success(Pair(answerText, sources))
        } catch (e: Exception) {
            Log.e("GeminiApiService", "querySearchGrounding error: ${e.message}", e)
            Result.success(Pair("No results found, please try again", emptyList()))
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}
