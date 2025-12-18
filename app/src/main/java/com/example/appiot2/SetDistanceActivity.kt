package com.example.appiot2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SetDistanceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_distance)

        val seekBarDistance = findViewById<SeekBar>(R.id.seekBarDistance)
        val buttonSetDistance = findViewById<Button>(R.id.buttonSetDistance)
        val textViewSelectedDistance = findViewById<TextView>(R.id.textViewSelectedDistance)

        val database = Firebase.database
        val distanceRef = database.getReference("configuraciones/distancia_programada")

        // Read initial value from Firebase
        distanceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val savedDistance = snapshot.getValue(Float::class.java) ?: 0.5f
                    val progress = ((savedDistance - 0.5f) / 0.5f).toInt()
                    seekBarDistance.progress = progress
                    textViewSelectedDistance.text = String.format("Distancia guardada: %.1f metros", savedDistance)
                } else {
                    textViewSelectedDistance.text = "Distancia: 0.5 metros"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, "Error al leer datos.", Toast.LENGTH_SHORT).show()
            }
        })

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

            distanceRef.setValue(distance).addOnSuccessListener {
                textViewSelectedDistance.text = String.format("Distancia guardada: %.1f metros", distance)
                Toast.makeText(this, "Distancia guardada en la nube.", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Error al guardar en la nube.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
