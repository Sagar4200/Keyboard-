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
        prefs = Prefs(this)

        setContentView(R.layout.activity_main)

        val etApiKey = findViewById<EditText>(R.id.etApiKey)
        val swAi = findViewById<Switch>(R.id.swAi)
        val sbHeight = findViewById<SeekBar>(R.id.sbHeight)
        val tvHeight = findViewById<TextView>(R.id.tvHeight)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnEnableIme = findViewById<Button>(R.id.btnEnableIme)

        etApiKey.setText(prefs.apiKey)
        swAi.isChecked = prefs.aiEnabled
        sbHeight.max = 240
        sbHeight.progress = (prefs.keyboardHeightDp - 180).coerceAtLeast(0)
        tvHeight.text = "Keyboard Height: ${prefs.keyboardHeightDp}dp"

        sbHeight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val dp = 180 + progress
                tvHeight.text = "Keyboard Height: ${dp}dp"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnSave.setOnClickListener {
            prefs.apiKey = etApiKey.text.toString()
            prefs.aiEnabled = swAi.isChecked
            prefs.keyboardHeightDp = 180 + sbHeight.progress
            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
        }

        btnEnableIme.setOnClickListener {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }
    }
}
