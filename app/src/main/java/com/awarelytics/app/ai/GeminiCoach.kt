package com.awarelytics.app.ai

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gemini API integration for asynchronous weekly analysis.
 *
 * Generates "Periodic Reflection Summaries" and acts as an empathetic coach.
 * Only receives anonymized, aggregated data — no raw timestamps or app names.
 *
 * Output: A 3-sentence non-judgmental reflection + one actionable goal in JSON format.
 */
@Singleton
class GeminiCoach @Inject constructor(
    private val weeklySummaryAggregator: WeeklySummaryAggregator
) {
    private val gson = Gson()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    companion object {
        private const val TAG = "GeminiCoach"

        private const val GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent"
    }

    /**
     * Data class for the structured weekly reflection output.
     */
    data class WeeklyReflection(
        val reflection_summary: String,
        val actionable_goal: String,
        val encouragement: String
    )

    /**
     * Generate a weekly reflection by:
     * 1. Aggregating Room DB data into anonymized summary
     * 2. Sending to Gemini with structured prompt
     * 3. Parsing JSON response
     */
    suspend fun generateWeeklyReflection(): Result<WeeklyReflection> {
        return withContext(Dispatchers.IO) {
            try {
                // Step 1: Aggregate anonymized data
                val summary = weeklySummaryAggregator.generateWeeklySummary()
                val summaryJson = gson.toJson(summary)

                // Step 2: Build the structured prompt
                val prompt = buildGeminiPrompt(summaryJson)

                // Step 3: Call Gemini API
                val response = callGeminiApi(prompt)

                // Step 4: Parse the response
                val reflection = parseReflectionResponse(response)

                Log.d(TAG, "Weekly reflection generated successfully")
                Result.success(reflection)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to generate weekly reflection: ${e.message}")
                Result.failure(e)
            }
        }
    }

    /**
     * Build the structured Gemini system prompt.
     *
     * Following the precise prompt engineering structure from the spec:
     * - System: Role definition (empathetic digital well-being coach)
     * - Input Data: Anonymized weekly summary
     * - Task: Generate reflection + goal in JSON format
     */
    private fun buildGeminiPrompt(summaryJson: String): String {
        return """
            System: Act as an empathetic digital well-being coach. You are warm, 
            non-judgmental, and supportive. Your tone should be like a caring friend 
            who genuinely wants to help the user be more present in their daily life. 
            Never use shaming language or make the user feel guilty.

            Input Data: $summaryJson

            Task: Based on the usage data above, generate:
            1. A 3-sentence, non-judgmental weekly reflection summary that:
               - Acknowledges the user's patterns without criticism
               - Connects observed behaviors to potential triggers
               - Highlights any positive progress or trends
            2. One small, actionable goal for next week that is:
               - Specific and measurable
               - Achievable in one week
               - Related to the most impactful pattern observed
            3. A brief encouragement message (1 sentence)

            Output STRICTLY in this JSON format (no markdown, no extra text):
            {
                "reflection_summary": "Three sentences about their week...",
                "actionable_goal": "One specific goal for next week...",
                "encouragement": "One encouraging sentence..."
            }
        """.trimIndent()
    }

    /**
     * Call the Gemini API with the structured prompt.
     */
    private fun callGeminiApi(prompt: String): String {
        val requestBody = gson.toJson(
            mapOf(
                "contents" to listOf(
                    mapOf(
                        "parts" to listOf(
                            mapOf("text" to prompt)
                        )
                    )
                ),
                "generationConfig" to mapOf(
                    "temperature" to 0.7,
                    "topK" to 40,
                    "topP" to 0.95,
                    "maxOutputTokens" to 512,
                    "responseMimeType" to "application/json"
                )
            )
        )

        val request = Request.Builder()
            .url("$GEMINI_API_URL?key=${com.awarelytics.app.BuildConfig.GEMINI_API_KEY}")
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .build()

        val response = httpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            throw Exception("Gemini API error: ${response.code} - ${response.message}")
        }

        return response.body?.string() ?: throw Exception("Empty response from Gemini API")
    }

    /**
     * Parse the Gemini API response to extract the WeeklyReflection.
     */
    private fun parseReflectionResponse(responseJson: String): WeeklyReflection {
        try {
            val jsonObject = JsonParser.parseString(responseJson).asJsonObject
            val candidates = jsonObject.getAsJsonArray("candidates")
            val content = candidates[0].asJsonObject
                .getAsJsonObject("content")
                .getAsJsonArray("parts")[0].asJsonObject
                .get("text").asString

            // Parse the inner JSON from Gemini's text output
            val reflection = gson.fromJson(content, WeeklyReflection::class.java)
            return reflection
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse Gemini response: ${e.message}")
            // Return a fallback reflection
            return WeeklyReflection(
                reflection_summary = "This week showed interesting patterns in your digital habits. " +
                        "Your usage data suggests there may be moments where being more present could enrich your connections. " +
                        "Every step toward awareness is progress worth celebrating.",
                actionable_goal = "Try putting your phone face-down during one meal per day this week.",
                encouragement = "You're already taking a great step by using Awarelytics! 🌟"
            )
        }
    }
}
