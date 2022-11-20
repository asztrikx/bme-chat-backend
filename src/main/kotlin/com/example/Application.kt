package com.example

import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.websocket.*
import org.ktorm.database.Database
import java.text.DateFormat
import java.time.Duration

// Entities are avoided as they don't work with serialization

val database = Database.connect(
    url="jdbc:sqlite:./chat.db",
)

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        // routing uses websocket, so it has to be installed earlier...
        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            masking = false
            contentConverter = GsonWebsocketContentConverter()
        }
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
