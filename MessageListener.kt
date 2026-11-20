package com.aikeyboard.app

import android.app.Notification
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

data class Incoming(
    val pkg: String,
    val sender: String,
    val text: String,
    val time: Long,
    val fromMe: Boolean
)

object MessageStore {
    private val items = ArrayList<Incoming>()

    @Synchronized
    fun add(m: Incoming): Boolean {
        val dup = items.takeLast(8).any {
            it.pkg == m.pkg && it.sender == m.sender && it.text == m.text && Math.abs(it.time - m.time) < 30000
        }
        if (dup) return false
        items.add(m)
        if (items.size > 80) items.removeAt(0)
        return true
    }

    @Synchronized
    fun recent(group: String, n: Int): List<Incoming> =
        items.filter { groupOf(it.pkg) == group }.sortedBy { it.time }.takeLast(n)

    fun groupOf(pkg: String): String = when {
        pkg.startsWith("com.whatsapp") -> "whatsapp"
        pkg.startsWith("org.telegram") || pkg.startsWith("org.thunderdog") -> "telegram"
        pkg.startsWith("com.facebook.orca") || pkg.startsWith("com.facebook.mlite") -> "messenger"
        pkg.contains("messaging") || pkg.contains("mms") || pkg.contains("sms") -> "sms"
        else -> pkg
    }
}

class MessageListener : NotificationListenerService() {

    private val main = Handler(Looper.getMainLooper())
    private val summaryRegex = Regex("^\\d+ (new )?messages?.*", RegexOption.IGNORE_CASE)

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        try { handle(sbn) } catch (_: Exception) { }
    }

    private fun handle(sbn: StatusBarNotification) {
        if (sbn.packageName == packageName) return
        val n = sbn.notification ?: return
        if (n.flags and Notification.FLAG_GROUP_SUMMARY != 0) return
        val extras = n.extras ?: return
        val pkg = sbn.packageName
        val isMessagingApp = MessageStore.groupOf(pkg) != pkg
        if (!isMessagingApp && n.category != Notification.CATEGORY_MESSAGE) return

        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val fresh = ArrayList<Incoming>()

        @Suppress("DEPRECATION")
        val msgs = extras.getParcelableArray("android.messages")
        if (msgs != null && msgs.isNotEmpty()) {
            for (p in msgs.takeLast(6)) {
                val b = p as? Bundle ?: continue
                val text = b.getCharSequence("text")?.toString() ?: continue
                val time = b.getLong("time", sbn.postTime)
                val personName: String? = if (android.os.Build.VERSION.SDK_INT >= 28)
                    (b.getParcelable<android.os.Parcelable>("sender_person") as? android.app.Person)?.name?.toString()
                else null
                val senderRaw = b.getCharSequence("sender")?.toString() ?: personName
                val fromMe = senderRaw == null
                val sender = senderRaw ?: "Me"
                val m = Incoming(pkg, if (sender.isBlank()) title else sender, text, time, fromMe)
                if (MessageStore.add(m)) fresh.add(m)
            }
        } else {
            val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: return
            if (text.isBlank() || summaryRegex.matches(text.trim())) return
            val m = Incoming(pkg, title.ifBlank { "Unknown" }, text, sbn.postTime, false)
            if (MessageStore.add(m)) fresh.add(m)
        }

        val last = fresh.lastOrNull { !it.fromMe } ?: return
        main.post { AiKeyboardService.instance?.onIncoming(last) }
    }
}
