package com.aikeyboard.app

import android.content.Context

class Prefs(ctx: Context) {
    private val sp = ctx.applicationContext.getSharedPreferences("ai_keyboard", Context.MODE_PRIVATE)

    companion object {
        val TONES = listOf("Friendly", "Formal", "Funny", "Short", "Romantic", "Professional")
    }

    var apiKey: String
        get() = sp.getString("api_key", "") ?: ""
        set(v) { sp.edit().putString("api_key", v.trim()).apply() }

    // Put the exact model name from Google AI Studio here (editable in app settings)
    var model: String
        get() = sp.getString("model", "gemini-2.5-flash") ?: "gemini-2.5-flash"
        set(v) { sp.edit().putString("model", v.trim()).apply() }

    var persona: String
        get() = sp.getString("persona", "") ?: ""
        set(v) { sp.edit().putString("persona", v.trim()).apply() }

    var tone: String
        get() = sp.getString("tone", "Friendly") ?: "Friendly"
        set(v) { sp.edit().putString("tone", v).apply() }

    var aiOn: Boolean
        get() = sp.getBoolean("ai_on", false)
        set(v) { sp.edit().putBoolean("ai_on", v).apply() }

    var autoSend: Boolean
        get() = sp.getBoolean("auto_send", true)
        set(v) { sp.edit().putBoolean("auto_send", v).apply() }

    var autoMode: Boolean
        get() = sp.getBoolean("auto_mode", false)
        set(v) { sp.edit().putBoolean("auto_mode", v).apply() }

    var haptic: Boolean
        get() = sp.getBoolean("haptic", true)
        set(v) { sp.edit().putBoolean("haptic", v).apply() }

    var numberRow: Boolean
        get() = sp.getBoolean("number_row", true)
        set(v) { sp.edit().putBoolean("number_row", v).apply() }

    /** 0 = follow system, 1 = light, 2 = dark */
    var theme: Int
        get() = sp.getInt("theme", 0)
        set(v) { sp.edit().putInt("theme", v).apply() }

    /** Keyboard height percent 60..140 */
    var heightPct: Int
        get() = sp.getInt("height_pct", 100)
        set(v) { sp.edit().putInt("height_pct", v.coerceIn(60, 140)).apply() }
}
