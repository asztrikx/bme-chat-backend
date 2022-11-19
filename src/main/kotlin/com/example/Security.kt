package com.example.plugins

import com.example.session.Session
import com.example.session.validate
import io.ktor.server.auth.*
import io.ktor.util.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import java.nio.charset.StandardCharsets
import java.util.*

fun Application.configureSecurity() {
    authentication {
        basic(name = "session") {
            realm = "LoginRealm"
            validate { credentials ->
                val token = String(Base64.getDecoder().decode(credentials.password), StandardCharsets.UTF_8)
                if (Session.validate(credentials.name.toInt(), token)) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}
