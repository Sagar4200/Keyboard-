package com.sagar4200.aikeyboard

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val prefs = Prefs(this)

            val scroll = ScrollView(this).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setBackgroundColor(Color.WHITE)
            }

            val root = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(48, 48, 48, 48)
            }

            val tvTitle = TextView(this).apply {
                text = "Sagar AI Keyboard Setup"
                textSize = 22f
                setTextColor(Color.BLACK)
            }

            val etApiKey = EditText(this).apply {
                hint = "Enter Gemini API Key"
                setText(prefs.apiKey)
                setTextColor(Color.BLACK)
                setHintTextColor(Color.GRAY)
            }

            val swAi = Switch(this).apply {
                text = "Enable AI Reply Mode"
                isChecked = prefs.aiEnabled
                setTextColor(Color.BLACK)
            }

            val tvHeight = TextView(this).apply {
                text = "Keyboard Height: ${prefs.keyboardHeightDp}dp"
                textSize = 16f
                setTextColor(Color.BLACK)
            }

            val sbHeight = SeekBar(this).apply {
                max = 240
                progress = (prefs.keyboardHeightDp - 180).coerceIn(0, 240)
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        tvHeight.text = "Keyboard Height: ${180 + progress}dp"
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                })
            }

            val btnSave = Button(this).apply {
                text = "Save Settings"
                setOnClickListener {
                    val key = etApiKey.text.toString().trim()
                    if (key.isNotEmpty()) {
                        prefs.apiKey = key
                    }
                    prefs.aiEnabled = swAi.isChecked
                    prefs.keyboardHeightDp = 180 + sbHeight.progress
                    Toast.makeText(this@MainActivity, "Settings saved", Toast.LENGTH_SHORT).show()
                }
            }

            val btnEnableIme = Button(this).apply {
                text = "Open Keyboard Settings"
                setOnClickListener {
                    try {
                        startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, e.message ?: "Cannot open settings", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val lpMargin = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 28
            }

            root.addView(tvTitle)
            root.addView(etApiKey, lpMargin)
            root.addView(swAi, lpMargin)
            root.addView(tvHeight, lpMargin)
            root.addView(sbHeight, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 14 })
            root.addView(btnSave, lpMargin)
            root.addView(btnEnableIme, lpMargin)

            scroll.addView(root)
            setContentView(scroll)
        } catch (t: Throwable) {
            val fallback = TextView(this).apply {
                text = "Error opening app:\n${t.javaClass.simpleName}: ${t.message}"
                textSize = 15f
                setTextColor(Color.RED)
                setPadding(40, 40, 40, 40)
            }
            setContentView(fallback)
        }
    }
}
