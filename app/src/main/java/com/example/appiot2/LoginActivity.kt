package com.example.appiot2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.DriverManager

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            lifecycleScope.launch {
                val result = withContext(Dispatchers.IO) {
                    try {
                        val dbConfig = DatabaseHelper()
                        // Removed explicit Class.forName() to rely on modern JDBC auto-discovery.
                        DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.pass)?.use { connection ->
                            val query = "SELECT * FROM Usuarios WHERE nombre_usuario = ? AND clave_usuario = ?"
                            connection.prepareStatement(query).use { preparedStatement ->
                                preparedStatement.setString(1, username)
                                preparedStatement.setString(2, password)
                                preparedStatement.executeQuery().use { resultSet ->
                                    if (resultSet.next()) "Success" else "InvalidCredentials"
                                }
                            }
                        } ?: "ConnectionError"
                    } catch (t: Throwable) { // Catch Throwable to prevent any crash
                        t.printStackTrace()
                        "Exception: ${t.message}"
                    }
                }

                progressBar.visibility = View.GONE
                when (result) {
                    "Success" -> {
                        Toast.makeText(this@LoginActivity, "Login exitoso", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("USERNAME", username)
                        startActivity(intent)
                        finish()
                    }
                    "InvalidCredentials" -> showErrorDialog("Error de Autenticación", "Credenciales inválidas")
                    "ConnectionError" -> showErrorDialog("Error de Conexión", "No se pudo conectar a la base de datos.")
                    else -> showErrorDialog("Error Inesperado", result)
                }
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
