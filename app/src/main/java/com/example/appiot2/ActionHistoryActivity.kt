package com.example.appiot2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ActionHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_history)

        val textViewHistoryLog = findViewById<TextView>(R.id.textViewHistoryLog)
        val database = Firebase.database
        val historyRef = database.getReference("historial")

        historyRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val historyLogs = mutableListOf<String>()
                for (childSnapshot in snapshot.children) {
                    val log = childSnapshot.getValue(String::class.java)
                    if (log != null) {
                        historyLogs.add(log)
                    }
                }

                if (historyLogs.isNotEmpty()) {
                    // Sort logs descending (newest first) and display them
                    textViewHistoryLog.text = historyLogs.sortedDescending().joinToString("\n\n")
                } else {
                    textViewHistoryLog.text = "No hay acciones registradas."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                textViewHistoryLog.text = "Error al cargar el historial."
            }
        })
    }
}
