package com.example.appiot2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Receive and store the username from LoginActivity
        username = intent.getStringExtra("USERNAME")

        val switchAutomaticMode = findViewById<Switch>(R.id.switchAutomaticMode)
        switchAutomaticMode.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) "Modo automático activado" else "Modo automático desactivado"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        val buttonScheduleNotifications = findViewById<Button>(R.id.buttonScheduleNotifications)
        buttonScheduleNotifications.setOnClickListener {
            val intent = Intent(this, ScheduleNotificationsActivity::class.java)
            startActivity(intent)
        }

        val buttonScheduleLights = findViewById<Button>(R.id.buttonScheduleLights)
        buttonScheduleLights.setOnClickListener {
            val intent = Intent(this, ScheduleLightsActivity::class.java)
            startActivity(intent)
        }

        val buttonSetDistance = findViewById<Button>(R.id.buttonSetDistance)
        buttonSetDistance.setOnClickListener {
            val intent = Intent(this, SetDistanceActivity::class.java)
            startActivity(intent)
        }

        val buttonViewSummary = findViewById<Button>(R.id.buttonViewSummary)
        buttonViewSummary.setOnClickListener {
            val intent = Intent(this, SummaryActivity::class.java)
            // Pass the username to SummaryActivity
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }
    }
}
