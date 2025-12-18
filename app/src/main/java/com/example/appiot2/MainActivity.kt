package com.example.appiot2

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var username: String? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var devicesStateRef: DatabaseReference

    private lateinit var switchFacadeLights: SwitchMaterial
    private lateinit var textViewFacadeLightsStatus: TextView
    private lateinit var buttonOpenGate: Button
    private lateinit var buttonCloseGate: Button
    private lateinit var textViewGateStatus: TextView

    private var listener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        username = intent.getStringExtra("USERNAME")
        
        // Initialize Firebase Database
        database = Firebase.database
        devicesStateRef = database.getReference("estado_dispositivos")

        // Initialize UI components
        switchFacadeLights = findViewById(R.id.switchFacadeLights)
        textViewFacadeLightsStatus = findViewById(R.id.textViewFacadeLightsStatus)
        buttonOpenGate = findViewById(R.id.buttonOpenGate)
        buttonCloseGate = findViewById(R.id.buttonCloseGate)
        textViewGateStatus = findViewById(R.id.textViewGateStatus)

        setupFacadeLights()
        setupGateControls()
        setupNavigationButtons()
        attachDatabaseReadListener()
    }

    private fun setupFacadeLights() {
        switchFacadeLights.setOnClickListener { view ->
            val isChecked = (view as SwitchMaterial).isChecked
            devicesStateRef.child("luces_fachada_on").setValue(isChecked)
                .addOnSuccessListener { logAction("Luces de fachada: ${if (isChecked) "Encendidas" else "Apagadas"}") }
        }
    }

    private fun setupGateControls() {
        buttonOpenGate.setOnClickListener {
            devicesStateRef.child("porton_abierto").setValue(true)
                .addOnSuccessListener { logAction("Portón: Abierto") }
        }

        buttonCloseGate.setOnClickListener {
            devicesStateRef.child("porton_abierto").setValue(false)
                .addOnSuccessListener { logAction("Portón: Cerrado") }
        }
    }

    private fun attachDatabaseReadListener() {
        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val lightsOn = snapshot.child("luces_fachada_on").getValue(Boolean::class.java) ?: false
                val gateOpen = snapshot.child("porton_abierto").getValue(Boolean::class.java) ?: false
                updateUI(lightsOn, gateOpen)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, "Failed to read value. ${error.toException()}", Toast.LENGTH_SHORT).show()
            }
        }
        devicesStateRef.addValueEventListener(listener!!)
    }

    private fun updateUI(lightsOn: Boolean, gateOpen: Boolean) {
        // Update Facade Lights UI
        switchFacadeLights.isChecked = lightsOn
        if (lightsOn) {
            textViewFacadeLightsStatus.text = "Estado: Encendidas"
            textViewFacadeLightsStatus.setTextColor(Color.parseColor("#4CAF50")) // Green
        } else {
            textViewFacadeLightsStatus.text = "Estado: Apagadas"
            textViewFacadeLightsStatus.setTextColor(Color.RED)
        }

        // Update Gate UI
        if (gateOpen) {
            textViewGateStatus.text = "Estado: Abierto"
            textViewGateStatus.setTextColor(Color.parseColor("#4CAF50")) // Green
        } else {
            textViewGateStatus.text = "Estado: Cerrado"
            textViewGateStatus.setTextColor(Color.RED)
        }
    }

    private fun logAction(action: String) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timestamp = sdf.format(Date())
        val logEntry = "$timestamp - $action"

        database.getReference("historial").push().setValue(logEntry)
    }

    private fun setupNavigationButtons() {
        // Omitted for brevity - your existing navigation code is fine
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detach the listener to avoid memory leaks
        if (listener != null) {
            devicesStateRef.removeEventListener(listener!!)
        }
    }
}
