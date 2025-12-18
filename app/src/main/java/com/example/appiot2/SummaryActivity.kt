package com.example.appiot2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SummaryActivity : AppCompatActivity() {

    private var listener: ValueEventListener? = null
    private lateinit var configRef: com.google.firebase.database.DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        val database = Firebase.database
        configRef = database.getReference("configuraciones")
        
        attachDatabaseReadListener()
    }

    private fun attachDatabaseReadListener() {
        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    updateSummaryUI(snapshot)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, "Error al leer el resumen.", Toast.LENGTH_SHORT).show()
            }
        }
        configRef.addValueEventListener(listener!!)
    }

    private fun updateSummaryUI(snapshot: DataSnapshot) {
        val textViewNotificationSummary = findViewById<TextView>(R.id.textViewNotificationSummary)
        val textViewLightsSummary = findViewById<TextView>(R.id.textViewLightsSummary)
        val textViewDistanceSummary = findViewById<TextView>(R.id.textViewDistanceSummary)

        // Update Notifications Summary
        val notifSnapshot = snapshot.child("programacion_notificaciones")
        if (notifSnapshot.exists()) {
            val startHour = notifSnapshot.child("start_hour").getValue(Int::class.java) ?: 0
            val startMinute = notifSnapshot.child("start_minute").getValue(Int::class.java) ?: 0
            val endHour = notifSnapshot.child("end_hour").getValue(Int::class.java) ?: 0
            val endMinute = notifSnapshot.child("end_minute").getValue(Int::class.java) ?: 0
            val formattedStart = String.format("%02d:%02d", startHour, startMinute)
            val formattedEnd = String.format("%02d:%02d", endHour, endMinute)
            textViewNotificationSummary.text = "Notificaciones: $formattedStart - $formattedEnd"
        } else {
            textViewNotificationSummary.text = "Notificaciones: No configurado"
        }

        // Update Lights Summary
        val lightsSnapshot = snapshot.child("programacion_luces")
        if (lightsSnapshot.exists()) {
            val startHour = lightsSnapshot.child("start_hour").getValue(Int::class.java) ?: 0
            val startMinute = lightsSnapshot.child("start_minute").getValue(Int::class.java) ?: 0
            val endHour = lightsSnapshot.child("end_hour").getValue(Int::class.java) ?: 0
            val endMinute = lightsSnapshot.child("end_minute").getValue(Int::class.java) ?: 0
            val formattedStart = String.format("%02d:%02d", startHour, startMinute)
            val formattedEnd = String.format("%02d:%02d", endHour, endMinute)
            textViewLightsSummary.text = "Luces: $formattedStart - $formattedEnd"
        } else {
            textViewLightsSummary.text = "Luces: No configurado"
        }

        // Update Distance Summary
        val distanceSnapshot = snapshot.child("distancia_programada")
        if (distanceSnapshot.exists()) {
            val distance = distanceSnapshot.getValue(Float::class.java) ?: 0.0f
            textViewDistanceSummary.text = String.format("Distancia: %.1f metros", distance)
        } else {
            textViewDistanceSummary.text = "Distancia: No configurado"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (listener != null) {
            configRef.removeEventListener(listener!!)
        }
    }
}
