package com.example.appiot2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker

class ScheduleLightsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_lights)

        val timePickerStart = findViewById<TimePicker>(R.id.timePickerStart)
        val timePickerEnd = findViewById<TimePicker>(R.id.timePickerEnd)
        val buttonSetLightSchedule = findViewById<Button>(R.id.buttonSetLightSchedule)
        val textViewSelectedTimeRange = findViewById<TextView>(R.id.textViewSelectedTimeRange)

        val sharedPreferences = getSharedPreferences("AppIoT2Prefs", Context.MODE_PRIVATE)
        val startHour = sharedPreferences.getInt("lights_start_hour", -1)
        val startMinute = sharedPreferences.getInt("lights_start_minute", -1)
        val endHour = sharedPreferences.getInt("lights_end_hour", -1)
        val endMinute = sharedPreferences.getInt("lights_end_minute", -1)

        if (startHour != -1) {
            timePickerStart.hour = startHour
            timePickerStart.minute = startMinute
            timePickerEnd.hour = endHour
            timePickerEnd.minute = endMinute
            val formattedStart = String.format("%02d:%02d", startHour, startMinute)
            val formattedEnd = String.format("%02d:%02d", endHour, endMinute)
            textViewSelectedTimeRange.text = "Rango guardado: $formattedStart - $formattedEnd"
        }

        buttonSetLightSchedule.setOnClickListener {
            val newStartHour = timePickerStart.hour
            val newStartMinute = timePickerStart.minute
            val newEndHour = timePickerEnd.hour
            val newEndMinute = timePickerEnd.minute

            val editor = sharedPreferences.edit()
            editor.putInt("lights_start_hour", newStartHour)
            editor.putInt("lights_start_minute", newStartMinute)
            editor.putInt("lights_end_hour", newEndHour)
            editor.putInt("lights_end_minute", newEndMinute)
            editor.apply()

            val formattedStart = String.format("%02d:%02d", newStartHour, newStartMinute)
            val formattedEnd = String.format("%02d:%02d", newEndHour, newEndMinute)
            textViewSelectedTimeRange.text = "Rango guardado: $formattedStart - $formattedEnd"
        }
    }
}
