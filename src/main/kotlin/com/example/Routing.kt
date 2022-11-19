package com.example.plugins

import com.example.contact.contact
import com.example.login.login
import com.example.logout.logout
import com.example.message.message
import com.example.register.register
import com.example.websocket.websocket
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("/api") {
            authenticate("session") {
                logout()
                contact()
                message()
            }
            login()
            register()
        }
        authenticate("session") {
            websocket()
        }
    }
}
