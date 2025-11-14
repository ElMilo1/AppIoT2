package com.example.appiot2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker

class ScheduleNotificationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_notifications)

        val timePickerStart = findViewById<TimePicker>(R.id.timePickerStart)
        val timePickerEnd = findViewById<TimePicker>(R.id.timePickerEnd)
        val buttonSetNotification = findViewById<Button>(R.id.buttonSetNotification)
        val textViewSelectedTime = findViewById<TextView>(R.id.textViewSelectedTime)

        val sharedPreferences = getSharedPreferences("AppIoT2Prefs", Context.MODE_PRIVATE)
        val startHour = sharedPreferences.getInt("notification_start_hour", -1)
        val startMinute = sharedPreferences.getInt("notification_start_minute", -1)
        val endHour = sharedPreferences.getInt("notification_end_hour", -1)
        val endMinute = sharedPreferences.getInt("notification_end_minute", -1)

        if (startHour != -1) {
            timePickerStart.hour = startHour
            timePickerStart.minute = startMinute
            timePickerEnd.hour = endHour
            timePickerEnd.minute = endMinute
            val formattedStart = String.format("%02d:%02d", startHour, startMinute)
            val formattedEnd = String.format("%02d:%02d", endHour, endMinute)
            textViewSelectedTime.text = "Rango guardado: $formattedStart - $formattedEnd"
        }

        buttonSetNotification.setOnClickListener {
            val newStartHour = timePickerStart.hour
            val newStartMinute = timePickerStart.minute
            val newEndHour = timePickerEnd.hour
            val newEndMinute = timePickerEnd.minute

            val editor = sharedPreferences.edit()
            editor.putInt("notification_start_hour", newStartHour)
            editor.putInt("notification_start_minute", newStartMinute)
            editor.putInt("notification_end_hour", newEndHour)
            editor.putInt("notification_end_minute", newEndMinute)
            editor.apply()

            val formattedStart = String.format("%02d:%02d", newStartHour, newStartMinute)
            val formattedEnd = String.format("%02d:%02d", newEndHour, newEndMinute)
            textViewSelectedTime.text = "Rango guardado: $formattedStart - $formattedEnd"
        }
    }
}
