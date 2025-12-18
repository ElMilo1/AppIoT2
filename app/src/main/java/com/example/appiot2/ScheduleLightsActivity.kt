package com.example.appiot2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ScheduleLightsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_lights)

        val timePickerStart = findViewById<TimePicker>(R.id.timePickerStart)
        val timePickerEnd = findViewById<TimePicker>(R.id.timePickerEnd)
        val buttonSetLightSchedule = findViewById<Button>(R.id.buttonSetLightSchedule)
        val textViewSelectedTimeRange = findViewById<TextView>(R.id.textViewSelectedTimeRange)

        val database = Firebase.database
        val lightsRef = database.getReference("configuraciones/programacion_luces")

        // Read initial values from Firebase
        lightsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val startHour = snapshot.child("start_hour").getValue(Int::class.java) ?: 19
                    val startMinute = snapshot.child("start_minute").getValue(Int::class.java) ?: 0
                    val endHour = snapshot.child("end_hour").getValue(Int::class.java) ?: 23
                    val endMinute = snapshot.child("end_minute").getValue(Int::class.java) ?: 59

                    timePickerStart.hour = startHour
                    timePickerStart.minute = startMinute
                    timePickerEnd.hour = endHour
                    timePickerEnd.minute = endMinute

                    val formattedStart = String.format("%02d:%02d", startHour, startMinute)
                    val formattedEnd = String.format("%02d:%02d", endHour, endMinute)
                    textViewSelectedTimeRange.text = "Rango guardado: $formattedStart - $formattedEnd"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, "Error al leer datos.", Toast.LENGTH_SHORT).show()
            }
        })

        buttonSetLightSchedule.setOnClickListener {
            val newStartHour = timePickerStart.hour
            val newStartMinute = timePickerStart.minute
            val newEndHour = timePickerEnd.hour
            val newEndMinute = timePickerEnd.minute

            val scheduleMap = mapOf(
                "start_hour" to newStartHour,
                "start_minute" to newStartMinute,
                "end_hour" to newEndHour,
                "end_minute" to newEndMinute
            )

            lightsRef.setValue(scheduleMap).addOnSuccessListener {
                val formattedStart = String.format("%02d:%02d", newStartHour, newStartMinute)
                val formattedEnd = String.format("%02d:%02d", newEndHour, newEndMinute)
                textViewSelectedTimeRange.text = "Rango guardado: $formattedStart - $formattedEnd"
                Toast.makeText(this, "Rango de luces guardado en la nube.", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Error al guardar en la nube.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
