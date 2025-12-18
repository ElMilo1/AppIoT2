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

class ScheduleNotificationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_notifications)

        val timePickerStart = findViewById<TimePicker>(R.id.timePickerStart)
        val timePickerEnd = findViewById<TimePicker>(R.id.timePickerEnd)
        val buttonSetNotification = findViewById<Button>(R.id.buttonSetNotification)
        val textViewSelectedTime = findViewById<TextView>(R.id.textViewSelectedTime)

        val database = Firebase.database
        val notificationRef = database.getReference("configuraciones/programacion_notificaciones")

        // Read initial values from Firebase
        notificationRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val startHour = snapshot.child("start_hour").getValue(Int::class.java) ?: 0
                    val startMinute = snapshot.child("start_minute").getValue(Int::class.java) ?: 0
                    val endHour = snapshot.child("end_hour").getValue(Int::class.java) ?: 23
                    val endMinute = snapshot.child("end_minute").getValue(Int::class.java) ?: 59

                    timePickerStart.hour = startHour
                    timePickerStart.minute = startMinute
                    timePickerEnd.hour = endHour
                    timePickerEnd.minute = endMinute

                    val formattedStart = String.format("%02d:%02d", startHour, startMinute)
                    val formattedEnd = String.format("%02d:%02d", endHour, endMinute)
                    textViewSelectedTime.text = "Rango guardado: $formattedStart - $formattedEnd"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, "Error al leer datos.", Toast.LENGTH_SHORT).show()
            }
        })

        buttonSetNotification.setOnClickListener {
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

            notificationRef.setValue(scheduleMap).addOnSuccessListener {
                val formattedStart = String.format("%02d:%02d", newStartHour, newStartMinute)
                val formattedEnd = String.format("%02d:%02d", newEndHour, newEndMinute)
                textViewSelectedTime.text = "Rango guardado: $formattedStart - $formattedEnd"
                Toast.makeText(this, "Rango de notificaciones guardado en la nube.", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Error al guardar en la nube.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
