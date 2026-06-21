package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Sends a query to the Gemini API. Falls back to smart rule-based guidance in offline or keyless mode.
     */
    suspend fun askGlowUpCoach(prompt: String, chatHistory: List<pairSenderText> = emptyList()): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        // Check if API key is empty or default placeholder
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey.contains("PLACEHOLDER")) {
            Log.w(TAG, "Gemini API key is not configured. Falling back to Elite Local Coach logic.")
            return@withContext getOfflineCoachResponse(prompt)
        }

        try {
            val systemInstructions = """
                You are GlowUpX AI Coach (v2026), an elite, premium, luxury coach specializing in male and female self-improvement: skincare (specifically active serums like Niacinamide, Retinol, Alpha Arbutin), haircare (specifically Nizoral, Minoxidil 5% temple regrowth, castor oil, finasteride 1mg), fitness (Push Pull Legs routine), water intake, and strict discipline.
                Speak in a highly inspiring, sophisticated, futuristic premium coach tone (Gen Z aesthetic: luxury, focused, clean, science-supported, high status).
                Never over-promise but always motivate intense consistency. Keep responses elegant, structured, clear, and highly practical.
            """.trimIndent()

            // Build payload using standard org.json classes
            val rootJson = JSONObject()
            val contentsArray = JSONArray()

            // Optional: Include Chat History (last 4 turns to avoid exceeding token limit/latency)
            val historyToUse = chatHistory.takeLast(6)
            for (msg in historyToUse) {
                val contentObj = JSONObject()
                contentObj.put("role", if (msg.isUser) "user" else "model")
                val partsArray = JSONArray()
                val partObj = JSONObject()
                partObj.put("text", msg.text)
                partsArray.put(partObj)
                contentObj.put("parts", partsArray)
                contentsArray.put(contentObj)
            }

            // Current user query
            val currentContentObj = JSONObject()
            currentContentObj.put("role", "user")
            val currentPartsArray = JSONArray()
            val currentPartObj = JSONObject()
            currentPartObj.put("text", prompt)
            currentPartsArray.put(currentPartObj)
            currentContentObj.put("parts", currentPartsArray)
            contentsArray.put(currentContentObj)

            rootJson.put("contents", contentsArray)

            // System instructions
            val systemInstructionObj = JSONObject()
            val instructionPartsArray = JSONArray()
            val instructionPartObj = JSONObject()
            instructionPartObj.put("text", systemInstructions)
            instructionPartsArray.put(instructionPartObj)
            systemInstructionObj.put("parts", instructionPartsArray)
            rootJson.put("systemInstruction", systemInstructionObj)

            // Generation config
            val generationConfig = JSONObject()
            generationConfig.put("temperature", 0.7)
            generationConfig.put("maxOutputTokens", 800)
            rootJson.put("generationConfig", generationConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = rootJson.toString().toRequestBody(mediaType)

            val url = "$BASE_URL?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Content-Type", "application/json")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val code = response.code
                    val errMsg = response.body?.string() ?: "Unknown Network Error"
                    Log.e(TAG, "Gemini API failure ($code): $errMsg")
                    return@withContext getOfflineCoachResponse(prompt) + "\n\n*(Note: Cloud AI is currently offline or key is rate-limited. Serving local elite engine.)*"
                }

                val responseBodyStr = response.body?.string()
                if (responseBodyStr.isNullOrEmpty()) {
                    return@withContext "Empty response from GlowUpX server. Keep grinding."
                }

                val responseJson = JSONObject(responseBodyStr)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.getJSONObject("content")
                    val parts = content.getJSONArray("parts")
                    if (parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).getString("text")
                    }
                }
                return@withContext "GlowUpX AI received your token, but could not decode the response contents. Track your stats and re-attempt."
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error in askGlowUpCoach API call", e)
            return@withContext getOfflineCoachResponse(prompt) + "\n\n*(Note: Running in Local Offline Mode. Check internet connection for Cloud sync.)*"
        }
    }

    /**
     * Local luxury rule-based AI engine when offline or no API Key is set.
     */
    fun getOfflineCoachResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("routine") || lower.contains("schedule") -> {
                """
                    🔱 **GLOWUPX OPERATING SYSTEM — ROUTINE ADVICE**
                    
                    Your 30-day program is divided into 4 key phases:
                    - **Week 1: Detox and Scalp Prep**: Wash off dirt with CeraVe Cleanser, apply Niacinamide morning, and clear roots with Nizoral.
                    - **Week 2: Active Repair & Dermarolling**: Use dermaroller 0.5mm twice a week only on temples. Follow with Minoxidil 5% to boost blood flow.
                    - **Week 3: Intense Glow Nourishment**: Introduce At-home Hydrafacial and Red Light Therapy.
                    - **Week 4: The Final Polish**: Mild chemical peels for tan removal, paired with deep hydration.
                    
                    *Coach Advice*: "Discipline is the only gap between who you are and who you want to be. Check off every task on your checklist."
                """.trimIndent()
            }
            lower.contains("shampoo") || lower.contains("hair") || lower.contains("regrowth") || lower.contains("temple") || lower.contains("minoxidil") -> {
                """
                    💇 **EXPERTMIND — HAIR & TEMPLE REGROWTH COACHING**
                    
                    For actual temple recovery, absolute consistency with the dual-minoxidil & finasteride schedule is required:
                    1. **Minoxidil 5%** triggers follicle growth by dilating blood vessels. Apply 1ml every morning and night.
                    2. **Finasteride 1mg** blocks DHT which shrinks temple hair. Take after dinner.
                    3. **Nizoral (Ketoconazole)** shampoo blocks scalp inflammation. Use only on Mon, Wed, Sat.
                    4. **Dermarolling (0.5mm)** creates micro-channels that increase minoxidil absorption by 5x. Use only on Day 10 and Day 27.
                    
                    *Coach Tip*: "Temple hair recovery feels slow. 3-6 months is the baseline. Do not miss a single day. Sleep 8 hours to boost recovery."
                """.trimIndent()
            }
            lower.contains("skin") || lower.contains("serum") || lower.contains("acne") || lower.contains("tan") || lower.contains("sunscreen") || lower.contains("ordinary") -> {
                """
                    ✨ **GLOWCORE — DERMATOLOGY COACHING**
                    
                    Your premium skincare protocol:
                    - **AM**: Cleanser → Niacinamide 10% (pore size, sebum regulation) → Neutrogena Hydro Boost → **ISDIN Sunscreen SPF 50**.
                    - **PM**: Cleanser → Active serum (Alpha Arbutin 2% on Mon/Thu; AHA/BHA Toner on Wed/Sat; Just Barrier Recovery on Tue/Fri/Sun) → Hydro Boost → Aloe Vera Gel.
                    
                    ⚠️ **CRITICAL SECRETS**: "Without Sunscreen SPF 50, all tan removal and active brightening acids will fail. Sun exposure destroys newly-revealed skin layers. Protect your barrier."
                """.trimIndent()
            }
            lower.contains("diet") || lower.contains("protein") || lower.contains("egg") || lower.contains("indian") || lower.contains("calorie") -> {
                """
                    🍳 **GLOWUPX FUEL SYSTEM — DEEP HIGH-PROTEIN DIET**
                    
                    To rebuild skin collagen and supply Keratin proteins for hair regrowth, aim for **1.5g of protein per kg of body weight**:
                    
                    - **High-Protein Options**: Large whole eggs, boiled chicken breast, Greek yogurt, paneer, sprouts, nuts, peanut butter.
                    - **Indian Budget Scale**: Double eggs with wheat roti, green moong dal, roasted chana, double-toned skim paneer.
                    - **Hydration Booster**: 3-4 liters of water helps carry amino acids to the hair roots and plumps skin cells.
                    
                    *Fitness Rule*: "A clean diet reduces internal inflammation. Skin clarity starts from the gut."
                """.trimIndent()
            }
            lower.contains("workout") || lower.contains("gym") || lower.contains("push") || lower.contains("pull") || lower.contains("legs") -> {
                """
                    🏋️ **ATHLETE ENGINE — PUSH PULL LEGS WORKOUT**
                    
                    Working out increases systemic blood circulation, delivering fresh oxygen directly to face skin and hair roots.
                    
                    - **Push Day**: Bench Press, Overhead Shoulder Press, Lateral Raises, Tricep dips.
                    - **Pull Day**: Lat pulldowns, Barbell rows, Bicep curls, Face pulls.
                    - **Legs Day**: Squats, Romanian Deadlifts, Calf raises.
                    
                    *Hormone Boost*: "Intense resistance training improves face circulation, sharper jawline, testosterone levels, and confidence. Keep an exercise timer and sleep 8 hours."
                """.trimIndent()
            }
            lower.contains("sleep") || lower.contains("alarm") || lower.contains("rest") -> {
                """
                    🛌 **REGEN SYSTEM — SLEEP & ALARM PROTOCOL**
                    
                    Cellular division in hair matrix and skin cells reaches its peak during REM and Deep sleep stages (10 PM to 2 AM).
                    - **Target**: 7.5 to 8.5 hours.
                    - **Smart Alarm**: Keep sleep regular.
                    - **Night Stack**: Finasteride 1mg, Vitamin E, MSM, and Magnesium Glycinate right before rest.
                    
                    *Operating System*: "Sleep is the ultimate natural steroid for skin recovery and hormonal health."
                """.trimIndent()
            }
            else -> {
                """
                    🔱 **GLOWUPX 2026 OPERATING SYSTEM**
                    
                    Congratulations on taking charge of your transformation. Ensure you complete your **Daily Checklist** to earn **Glow Points**.
                    
                    Ask me specific questions regarding:
                    - **Skincare** (acne, tan removal, barrier repair, sunscreen, retinol)
                    - **Hair Regrowth** (Minoxidil 5% on temples, finasteride, Nizoral, dermarolling)
                    - **Diet & Fuel** (High protein, Indian recipes, muscle gain, hydration)
                    - **Workout Splits** (Push-Pull-Legs, jawline circulation)
                    - **Sleep & Habits** (MSM, Magnesium Glycinate, Alarms, daily tracking)
                    
                    *Coach Pro-tip*: "Your self-improvement is non-negotiable. Build raw discipline."
                """.trimIndent()
            }
        }
    }
}

data class pairSenderText(val isUser: Boolean, val text: String)
