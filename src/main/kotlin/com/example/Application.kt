package com.example

import com.example.plugins.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import org.ktorm.database.Database
import java.text.DateFormat

// Entities are avoided as they don't work with serialization

val database = Database.connect(
    url="jdbc:sqlite:/mnt/hdd/File/Study/BME/Szabvál/Kotlin/hf/ChatBackend/chat.db",
)

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureSecurity()
        configureRouting()
        install(ContentNegotiation) {
            gson {
                setDateFormat(DateFormat.LONG)
                setPrettyPrinting()
            }
        }
    }.start(wait = true)
}
