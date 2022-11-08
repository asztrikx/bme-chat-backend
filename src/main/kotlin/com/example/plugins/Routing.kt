package com.example.plugins

import com.example.user.users
import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.database.Database
import org.ktorm.entity.first

val database = Database.connect(
    url="jdbc:sqlite:/mnt/hdd/File/Study/BME/Szabvál/Kotlin/hf/ChatBackend/chat.db",
)

fun Application.configureRouting() {
    routing {
        get("/api/users") {
            // Listaként működik
            val user = database.users.first()

            call.respond(user)
        }
    }
}
