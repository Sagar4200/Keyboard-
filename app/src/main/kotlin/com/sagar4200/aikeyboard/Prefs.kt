package com.sagar4200.aikeyboard

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ai_keyboard_prefs", Context.MODE_PRIVATE)

    companion object {
        // Ekhane tomar Gemini API key paste koro:
        const val DEFAULT_API_KEY = "AQ.Ab8RN6JrLdUpGGvu6wHfi82ci3v7T__L2NBBvtKdAKi-iwSpvA"
    }

    var apiKey: String
        get() {
            val saved = prefs.getString("gemini_api_key", "")?.trim() ?: ""
            if (saved.isNotEmpty()) return saved
            return if (DEFAULT_API_KEY != "AQ.Ab8RN6JrLdUpGGvu6wHfi82ci3v7T__L2NBBvtKdAKi-iwSpvA" && DEFAULT_API_KEY.isNotBlank()) {
                DEFAULT_API_KEY.trim()
            } else {
                ""
            }
        }
        set(value) = prefs.edit().putString("gemini_api_key", value.trim()).apply()

    var aiEnabled: Boolean
        get() = prefs.getBoolean("ai_enabled", true)
        set(value) = prefs.edit().putBoolean("ai_enabled", value).apply()

    var keyboardHeightDp: Int
        get() = prefs.getInt("keyboard_height_dp", 260)
        set(value) = prefs.edit().putInt("keyboard_height_dp", value.coerceIn(180, 420)).apply()
}
