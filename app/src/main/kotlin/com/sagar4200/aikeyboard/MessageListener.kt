package com.sagar4200.aikeyboard

class MessageListener(private val prefs: Prefs) {

    fun canUseAi(): Boolean {
        return prefs.aiEnabled && prefs.apiKey.isNotBlank()
    }

    fun buildSmartReplyPrompt(incomingOrContextText: String): String {
        val cleanText = incomingOrContextText.trim()
        val contextPart = if (cleanText.isNotEmpty()) {
            "Context/Message:\n\"\"\"$cleanText\"\"\""
        } else {
            "No prior message text; write a short, natural greeting or helpful reply."
        }
        return """
You are an assistant inside an Android keyboard.
Generate a concise, natural reply matching the tone of the conversation.
Return ONLY the reply text without quotes or explanations.

$contextPart
        """.trimIndent()
    }
}
