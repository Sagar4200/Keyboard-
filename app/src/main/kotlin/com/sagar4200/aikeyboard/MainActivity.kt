package com.sagar4200.aikeyboard

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)
            prefs = Prefs(this)

            val etApiKey = findViewById<EditText>(R.id.etApiKey)
            val swAi = findViewById<SwitchCompat>(R.id.swAi)
            val tvHeight = findViewById<TextView>(R.id.tvHeight)
            val sbHeight = findViewById<SeekBar>(R.id.sbHeight)
            val btnSave = findViewById<Button>(R.id.btnSave)
            val btnEnableIme = findViewById<Button>(R.id.btnEnableIme)

            etApiKey.setText(prefs.apiKey)
            swAi.isChecked = prefs.aiEnabled

            sbHeight.max = 240
            sbHeight.progress = (prefs.keyboardHeightDp - 180).coerceIn(0, 240)
            tvHeight.text = "Keyboard Height: ${prefs.keyboardHeightDp}dp"

            sbHeight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    tvHeight.text = "Keyboard Height: ${180 + progress}dp"
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
        } catch (e: Exception) {
            Toast.makeText(this, "Init error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
