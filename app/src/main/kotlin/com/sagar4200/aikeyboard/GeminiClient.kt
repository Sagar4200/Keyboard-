package com.sagar4200.aikeyboard

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class GeminiClient(private val rawApiKey: String) {

    suspend fun generateReply(promptText: String): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = rawApiKey.trim()
        if (apiKey.isBlank() || apiKey == "AQ.Ab8RN6JrLdUpGGvu6wHfi82ci3v7T__L2NBBvtKdAKi-iwSpvA") {
            return@withContext Result.failure(IllegalArgumentException("AQ.Ab8RN6JrLdUpGGvu6wHfi82ci3v7T__L2NBBvtKdAKi-iwSpvA"))
        }

        var connection: HttpURLConnection? = null
        try {
            val encodedKey = URLEncoder.encode(apiKey, "UTF-8")
            val endpoint =
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$encodedKey"
            val url = URL(endpoint)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                doOutput = true
                connectTimeout = 15000
                readTimeout = 20000
            }

            val payload = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", promptText))
                        })
                    })
                })
            }

            OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                writer.write(payload.toString())
                writer.flush()
            }

            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val responseBody = BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).use { it.readText() }

            if (code !in 200..299) {
                return@withContext Result.failure(RuntimeException("HTTP $code: $responseBody"))
            }

            val json = JSONObject(responseBody)
            val candidates = json.optJSONArray("candidates")
            val text = candidates
                ?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text")
                ?.trim()

            if (text.isNullOrEmpty()) {
                Result.failure(RuntimeException("Empty response from Gemini"))
            } else {
                Result.success(text)
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            connection?.disconnect()
        }
    }
}
