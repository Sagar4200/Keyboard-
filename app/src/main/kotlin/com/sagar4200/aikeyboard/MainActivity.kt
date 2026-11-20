package com.sagar4200.aikeyboard

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = Prefs(this)

        val etApiKey = findViewById<EditText>(R.id.etApiKey)
        val swAi = findViewById<Switch>(R.id.swAi)
        val tvHeight = findViewById<TextView>(R.id.tvHeight)
        val sbHeight = findViewById<SeekBar>(R.id.sbHeight)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnEnableIme = findViewById<Button>(R.id.btnEnableIme)

        etApiKey.setText(prefs.apiKey)
        swAi.isChecked = prefs.aiEnabled

        sbHeight.max = 240 // 180dp + 240 = 420dp
        val initialProgress = (prefs.keyboardHeightDp - 180).coerceIn(0, 240)
        sbHeight.progress = initialProgress
        tvHeight.text = "Keyboard Height: ${prefs.keyboardHeightDp}dp"

        sbHeight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val heightDp = 180 + progress
                tvHeight.text = "Keyboard Height: ${heightDp}dp"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnSave.setOnClickListener {
            val enteredKey = etApiKey.text.toString().trim()
            if (enteredKey.isNotEmpty()) {
                prefs.apiKey = enteredKey
            }
            prefs.aiEnabled = swAi.isChecked
            prefs.keyboardHeightDp = 180 + sbHeight.progress
            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
        }

        btnEnableIme.setOnClickListener {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }
    }
}
