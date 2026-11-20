package com.aikeyboard.app

import android.os.Handler
import android.os.Looper
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

object GeminiClient {
    private val pool = Executors.newCachedThreadPool()
    private val main = Handler(Looper.getMainLooper())

    /** done(text, error) is always called on the main thread. */
    fun generate(apiKey: String, model: String, system: String, prompt: String, done: (String?, String?) -> Unit) {
        pool.execute {
            var result: String? = null
            var error: String? = null
            try {
                val url = URL("https://generativelanguage.googleapis.com/v1beta/models/${model.trim()}:generateContent")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.connectTimeout = 15000
                conn.readTimeout = 60000
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json")
                conn.setRequestProperty("x-goog-api-key", apiKey)

                val body = JSONObject()
                    .put("systemInstruction", JSONObject().put("parts", JSONArray().put(JSONObject().put("text", system))))
                    .put("contents", JSONArray().put(
                        JSONObject().put("role", "user")
                            .put("parts", JSONArray().put(JSONObject().put("text", prompt)))
                    ))
                    .put("generationConfig", JSONObject().put("temperature", 0.8).put("maxOutputTokens", 2048))

                conn.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }

                val code = conn.responseCode
                val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                val resp = stream?.bufferedReader()?.use { it.readText() } ?: ""

                if (code in 200..299) {
                    val parts = JSONObject(resp).getJSONArray("candidates")
                        .getJSONObject(0).getJSONObject("content").getJSONArray("parts")
                    val sb = StringBuilder()
                    for (i in 0 until parts.length()) sb.append(parts.getJSONObject(i).optString("text"))
                    var t = sb.toString().trim()
                    if (t.length >= 2 && t.startsWith("\"") && t.endsWith("\"")) t = t.substring(1, t.length - 1).trim()
                    if (t.isEmpty()) error = "Empty reply from model" else result = t
                } else {
                    error = try {
                        JSONObject(resp).getJSONObject("error").getString("message")
                    } catch (e: Exception) { "HTTP $code" }
                }
            } catch (e: Exception) {
                error = e.message ?: "Network error"
            }
            val r = result
            val er = error
            main.post { done(r, er) }
        }
    }
}
