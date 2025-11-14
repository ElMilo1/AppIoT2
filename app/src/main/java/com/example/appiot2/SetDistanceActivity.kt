package com.example.appiot2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView

class SetDistanceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_distance)

        val seekBarDistance = findViewById<SeekBar>(R.id.seekBarDistance)
        val buttonSetDistance = findViewById<Button>(R.id.buttonSetDistance)
        val textViewSelectedDistance = findViewById<TextView>(R.id.textViewSelectedDistance)

        val sharedPreferences = getSharedPreferences("AppIoT2Prefs", Context.MODE_PRIVATE)
        val savedDistance = sharedPreferences.getFloat("distance", -1f)

        if (savedDistance != -1f) {
            val progress = ((savedDistance - 0.5f) / 0.5f).toInt()
            seekBarDistance.progress = progress
            textViewSelectedDistance.text = String.format("Distancia guardada: %.1f metros", savedDistance)
        } else {
            textViewSelectedDistance.text = "Distancia: 0.5 metros"
        }

        seekBarDistance.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val distance = 0.5f + progress * 0.5f
                textViewSelectedDistance.text = String.format("Distancia: %.1f metros", distance)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        buttonSetDistance.setOnClickListener {
            val progress = seekBarDistance.progress
            val distance = 0.5f + progress * 0.5f
            val editor = sharedPreferences.edit()
            editor.putFloat("distance", distance)
            editor.apply()
            textViewSelectedDistance.text = String.format("Distancia guardada: %.1f metros", distance)
        }
    }
}
