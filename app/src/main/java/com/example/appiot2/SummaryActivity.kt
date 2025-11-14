package com.example.appiot2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.DriverManager
import java.sql.Time

class SummaryActivity : AppCompatActivity() {
    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        username = intent.getStringExtra("USERNAME")

        val buttonUpload = findViewById<Button>(R.id.buttonUpload)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarSummary)

        buttonUpload.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            uploadConfiguration()
        }
    }

    override fun onResume() {
        super.onResume()
        loadSummary()
    }

    private fun loadSummary() {
        val textViewNotificationSummary = findViewById<TextView>(R.id.textViewNotificationSummary)
        val textViewLightsSummary = findViewById<TextView>(R.id.textViewLightsSummary)
        val textViewDistanceSummary = findViewById<TextView>(R.id.textViewDistanceSummary)

        val sharedPreferences = getSharedPreferences("AppIoT2Prefs", Context.MODE_PRIVATE)

        val notificationStartHour = sharedPreferences.getInt("notification_start_hour", -1)
        val notificationStartMinute = sharedPreferences.getInt("notification_start_minute", -1)
        val notificationEndHour = sharedPreferences.getInt("notification_end_hour", -1)
        val notificationEndMinute = sharedPreferences.getInt("notification_end_minute", -1)
        if (notificationStartHour != -1) {
            val formattedStart = String.format("%02d:%02d", notificationStartHour, notificationStartMinute)
            val formattedEnd = String.format("%02d:%02d", notificationEndHour, notificationEndMinute)
            textViewNotificationSummary.text = "Notificaciones: $formattedStart - $formattedEnd"
        } else {
            textViewNotificationSummary.text = "Notificaciones: Aún no configurado"
        }

        val startHour = sharedPreferences.getInt("lights_start_hour", -1)
        val startMinute = sharedPreferences.getInt("lights_start_minute", -1)
        val endHour = sharedPreferences.getInt("lights_end_hour", -1)
        val endMinute = sharedPreferences.getInt("lights_end_minute", -1)
        if (startHour != -1) {
            val formattedStart = String.format("%02d:%02d", startHour, startMinute)
            val formattedEnd = String.format("%02d:%02d", endHour, endMinute)
            textViewLightsSummary.text = "Luces: $formattedStart - $formattedEnd"
        } else {
            textViewLightsSummary.text = "Luces: Aún no configurado"
        }

        val distance = sharedPreferences.getFloat("distance", -1f)
        if (distance != -1f) {
            textViewDistanceSummary.text = String.format("Distancia: %.1f metros", distance)
        } else {
            textViewDistanceSummary.text = "Distancia: Aún no configurado"
        }
    }

    private fun uploadConfiguration() {
        lifecycleScope.launch {
            val sharedPreferences = getSharedPreferences("AppIoT2Prefs", Context.MODE_PRIVATE)
            val progressBar = findViewById<ProgressBar>(R.id.progressBarSummary)

            val result = withContext(Dispatchers.IO) {
                try {
                    val dbConfig = DatabaseHelper()
                    // Use the correct, older driver class name for version 5.1.x
                    Class.forName("com.mysql.jdbc.Driver")
                    DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.pass)?.use { connection ->
                        val query = "INSERT INTO Configuraciones (Usuario, notif_inicio, notif_final, luces_inicio, luces_final, distancia_porton) VALUES (?, ?, ?, ?, ?, ?)"
                        connection.prepareStatement(query).use { preparedStatement ->
                            val notificationStartHour = sharedPreferences.getInt("notification_start_hour", -1)
                            val notificationStartMinute = sharedPreferences.getInt("notification_start_minute", -1)
                            val notificationEndHour = sharedPreferences.getInt("notification_end_hour", -1)
                            val notificationEndMinute = sharedPreferences.getInt("notification_end_minute", -1)

                            val lightsStartHour = sharedPreferences.getInt("lights_start_hour", -1)
                            val lightsStartMinute = sharedPreferences.getInt("lights_start_minute", -1)
                            val lightsEndHour = sharedPreferences.getInt("lights_end_hour", -1)
                            val lightsEndMinute = sharedPreferences.getInt("lights_end_minute", -1)

                            val distance = sharedPreferences.getFloat("distance", -1f)

                            // Safely format time as a 'HH:mm:ss' String for the database
                            val notifStartTimeStr = if (notificationStartHour != -1) String.format("%02d:%02d:00", notificationStartHour, notificationStartMinute) else null
                            val notifEndTimeStr = if (notificationEndHour != -1) String.format("%02d:%02d:00", notificationEndHour, notificationEndMinute) else null
                            val lightsStartTimeStr = if (lightsStartHour != -1) String.format("%02d:%02d:00", lightsStartHour, lightsStartMinute) else null
                            val lightsEndTimeStr = if (lightsEndHour != -1) String.format("%02d:%02d:00", lightsEndHour, lightsEndMinute) else null

                            preparedStatement.setString(1, username)
                            preparedStatement.setString(2, notifStartTimeStr)
                            preparedStatement.setString(3, notifEndTimeStr)
                            preparedStatement.setString(4, lightsStartTimeStr)
                            preparedStatement.setString(5, lightsEndTimeStr)
                            preparedStatement.setFloat(6, if (distance != -1f) distance else 0f)

                            preparedStatement.executeUpdate()
                            "Success"
                        }
                    } ?: "ConnectionError"
                } catch (t: Throwable) { // Catch Throwable to prevent any crash
                    t.printStackTrace()
                    "Exception: ${t.message}"
                }
            }

            progressBar.visibility = View.GONE
            when (result) {
                "Success" -> Toast.makeText(this@SummaryActivity, "Configuración subida con éxito", Toast.LENGTH_SHORT).show()
                "ConnectionError" -> showErrorDialog("Error de Conexión", "No se pudo conectar a la base de datos.")
                else -> showErrorDialog("Error Inesperado", result)
            }
        }
    }

    private fun showErrorDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Aceptar", null)
            .show()
    }
}
