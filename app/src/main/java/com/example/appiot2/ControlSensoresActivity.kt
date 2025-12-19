package com.example.appiot2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ControlSensoresActivity : AppCompatActivity() {

    private lateinit var sensorsRef: DatabaseReference
    private var sensorListener: ValueEventListener? = null

    private lateinit var switchSensorLights: SwitchMaterial
    private lateinit var switchSensorGate: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_sensores)

        switchSensorLights = findViewById(R.id.switchSensorLights)
        switchSensorGate = findViewById(R.id.switchSensorGate)

        val database = Firebase.database
        sensorsRef = database.getReference("configuraciones/sensores")

        setupListeners()
        attachDatabaseReadListener()
    }

    private fun setupListeners() {
        switchSensorLights.setOnCheckedChangeListener { _, isChecked ->
            sensorsRef.child("luces_sensor_on").setValue(isChecked)
                .addOnFailureListener { 
                    Toast.makeText(this, "Error al guardar estado del sensor de luces", Toast.LENGTH_SHORT).show() 
                }
        }

        switchSensorGate.setOnCheckedChangeListener { _, isChecked ->
            sensorsRef.child("porton_sensor_on").setValue(isChecked)
                .addOnFailureListener { 
                    Toast.makeText(this, "Error al guardar estado del sensor de portón", Toast.LENGTH_SHORT).show() 
                }
        }
    }

    private fun attachDatabaseReadListener() {
        sensorListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val lightsSensorOn = snapshot.child("luces_sensor_on").getValue(Boolean::class.java) ?: false
                    val gateSensorOn = snapshot.child("porton_sensor_on").getValue(Boolean::class.java) ?: false
                    
                    // Update UI without triggering the listeners again
                    switchSensorLights.isChecked = lightsSensorOn
                    switchSensorGate.isChecked = gateSensorOn
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, "Error al leer estado de los sensores.", Toast.LENGTH_SHORT).show()
            }
        }
        sensorsRef.addValueEventListener(sensorListener!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detach the listener to avoid memory leaks
        if (sensorListener != null) {
            sensorsRef.removeEventListener(sensorListener!!)
        }
    }
}
