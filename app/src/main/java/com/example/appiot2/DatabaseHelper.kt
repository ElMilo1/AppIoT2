package com.example.appiot2

// This class now acts as a simple and safe data container.
class DatabaseHelper {
    val host = "sql10.freesqldatabase.com"
    val database = "sql10807698"
    val port = 3306
    val user = "sql10807698"
    val pass = "yDhBwDk44V"
    val url = "jdbc:mysql://$host:$port/$database?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
}
