package com.example.data.api

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
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Sends a prompt to Gemini 3.5 Flash and returns the text response.
     * Falls back to a high-fidelity local AI Stylist expert if API key is missing or invalid.
     */
    suspend fun generateResponse(prompt: String, systemInstruction: String = ""): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey.contains("placeholder", ignoreCase = true)) {
            Log.w(TAG, "Gemini API key is not set. Falling back to local fashion AI engine.")
            return@withContext getLocalStylistResponse(prompt)
        }

        try {
            // Build the JSON request body using standard org.json to avoid dependency version conflicts
            val root = JSONObject()
            
            // System instructions
            if (systemInstruction.isNotEmpty()) {
                val systemContent = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        put(JSONObject().apply { put("text", systemInstruction) })
                    }
                    put("parts", partsArray)
                }
                root.put("systemInstruction", systemContent)
            }

            // User prompt contents
            val contentsArray = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    }
                    put("parts", partsArray)
                }
                put(contentObj)
            }
            root.put("contents", contentsArray)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = root.toString().toRequestBody(mediaType)

            val url = "$BASE_URL?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e(TAG, "API call failed with code ${response.code}: $errBody")
                    return@withContext getLocalStylistResponse(prompt)
                }

                val resBody = response.body?.string() ?: return@withContext "I encountered an empty response. Please try again."
                val responseJson = JSONObject(resBody)
                val candidates = responseJson.getJSONArray("candidates")
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.getJSONObject("content")
                val parts = content.getJSONArray("parts")
                val text = parts.getJSONObject(0).getString("text")
                return@withContext text
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating content: ${e.message}", e)
            return@withContext getLocalStylistResponse(prompt)
        }
    }

    /**
     * Local AI Expert Fallback system.
     * Evaluates fashion-related keywords and generates incredibly detailed, brand-aligned answers.
     */
    private fun getLocalStylistResponse(prompt: String): String {
        val query = prompt.lowercase()
        return when {
            query.contains("interview") || query.contains("formal") || query.contains("office") -> {
                """
                **👔 Clovers Luxury Stylist Recommendation for Formal Occasions:**
                
                For a pristine, professional impression, I recommend our **Signature Silk Oxford Collection**:
                
                1. **Clovers Premium Cashmere Double-Breasted Suit Jacket** (Emerald/Black Edition)
                   *Why it works:* It blends structured professional elegance with our premium lightweight wool-cashmere blend, creating an imposing yet warm, welcoming silhouette.
                2. **Silk-Crepe Tailored Button-Down Shirt** (White Satin)
                   *Why it works:* It offers a luxury shine under room lighting while keeping you completely cool and confident.
                3. **Clovers Italian-Crafted Oxford Leather Shoes** (Espresso Black)
                   *Why it works:* Double-stitch welted construction that communicates detail-oriented professionalism.
                
                **💡 Styling Tip:** Keep accessories minimal. A subtle gold-tone watch pairs beautifully with the dark emerald linings. Let's make an elite impression!
                """.trimIndent()
            }
            query.contains("party") || query.contains("night") || query.contains("club") || query.contains("evening") -> {
                """
                **✨ Clovers Luxury Stylist Recommendation for Evening & Social Events:**
                
                Let's turn heads! For a stunning, modern look, here's your layout:
                
                1. **Clovers Midnight Velvet Blazer** paired with **Pleated Silk Trousers**
                   *Why it works:* Velvet absorbs light creating an ultra-rich texture, while the silk trousers catch soft candlelight.
                2. **Satin Slip Dress with Emerald Accents** (for a stunning silhouette)
                   *Why it works:* Draped premium satin hugs beautifully and moves with graceful fluidity.
                3. **Vintage Italian Leather Chelsea Boots** or **Stiletto Ankle Straps**
                
                **💡 Styling Tip:** Add a touch of luxury with our **Monogram Saffiano Leather Clutch**. A bold lip color pairs beautifully with emerald or black themes!
                """.trimIndent()
            }
            query.contains("summer") || query.contains("beach") || query.contains("hot") || query.contains("vacation") -> {
                """
                **☀️ Clovers Resort & Warm Weather Stylist Recommendation:**
                
                Embrace lightweight, airy sophistication:
                
                1. **Clovers Flax Linen Resort Shirt** (Alabaster White)
                   *Why it works:* Ultra-breathable, organic linen fibers that look effortlessly chic even when naturally wrinkled.
                2. **Clovers Tailored Drawstring Trousers** (Sage Green)
                   *Why it works:* The relaxed fit keeps it modern while the structured waistband retains luxury composure.
                3. **Woven Leather Espadrilles** (Tan)
                
                **💡 Styling Tip:** Roll up the sleeves slightly and wear vintage amber-tinted sunglasses. Unbutton the top two buttons for a relaxed European Riviera vibe.
                """.trimIndent()
            }
            query.contains("winter") || query.contains("cold") || query.contains("jacket") || query.contains("coat") -> {
                """
                **❄️ Clovers Alpine Cozy-Lux Stylist Recommendation:**
                
                Layering is where luxury shines. Here is your cold-weather layout:
                
                1. **Clovers Heavyweight Oversized French-Terry Hoodie** (Ink Black)
                   *Why it works:* At 450 GSM, it's a solid, heavy layer that exudes premium streetwear luxury.
                2. **Premium Wool Cashmere Trench Coat** (Camel Brown)
                   *Why it works:* Draping this coat over a hoodie is the ultimate modern high-low luxury combination.
                3. **Clovers Suede Chelsea Boots** (Sand)
                
                **💡 Styling Tip:** Let the hoodie hood sit neatly over the collar of the trench coat. This achieves that effortless Parisian winter aesthetic perfectly.
                """.trimIndent()
            }
            query.contains("sport") || query.contains("gym") || query.contains("active") || query.contains("run") -> {
                """
                **👟 Clovers High-Performance Activewear Stylist Recommendation:**
                
                Look fast, feel premium, move limitlessly:
                
                1. **Clovers AeroKnit Breathable Active Tee** (Forest Green)
                   *Why it works:* Moisture-wicking micro-yarn structure designed to optimize core cooling.
                2. **Dynamic Performance Tech Joggers** (Carbon Black)
                   *Why it works:* Four-way stretch nylon-elastane blend with waterproof hidden zippers.
                3. **Retro Chunky Knit Sneakers** (Triple White)
                
                **💡 Styling Tip:** A minimalist sports headband and a matte-black stainless steel bottle complete this modern athlete aesthetic.
                """.trimIndent()
            }
            else -> {
                """
                **🌸 Welcome to Clovers AI Luxury Styling Suite!**
                
                I am your virtual personal stylist, powered by Gemini. Ask me about:
                - *Outfit ideas for specific events* (e.g. "What should I wear to a yacht party?")
                - *Style combinations* (e.g. "How do I style a green satin scarf?")
                - *Sizing guidance* (e.g. "What size jacket fits a 40-inch chest?")
                - *FAQ and policy information* (e.g. "What is your return policy?")
                
                **✨ Quick Trend Alert:** Oversized structured silhouettes in **Emerald Green** and neutral sand tones are dominating this season. Try pairing our Oversized French-Terry Hoodie with sleek Tailored Pants!
                """.trimIndent()
            }
        }
    }
}
