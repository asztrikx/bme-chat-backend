package com.example.plugins

import com.example.session.Session
import com.example.session.base64Decode
import com.example.session.validate
import io.ktor.server.auth.*
import io.ktor.server.application.*

fun Application.configureSecurity() {
    authentication {
        basic(name = "session") {
            realm = "LoginRealm"
            validate { credentials ->
                val token = credentials.password.base64Decode()
                if (Session.validate(credentials.name.toInt(), token)) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}
