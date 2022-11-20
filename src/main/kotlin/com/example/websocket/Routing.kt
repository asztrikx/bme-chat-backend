package com.example.websocket

import com.example.message.NewMessage
import com.example.message.handleNewMessage
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

fun Route.websocket() {
	route("/ws") {
		webSocket {
			val principal = call.principal<UserIdPrincipal>()!!
			val userId = principal.name.toInt()

			WebsocketsManager.addConnection(userId, this)
			try {
				// do not let connection close
				while (true) {
					val newMessage = receiveDeserialized<NewMessage>()
					handleNewMessage(userId, newMessage)
				}
			} catch (_: Exception) {} finally {
				WebsocketsManager.removeConnection(userId)
			}
		}
 	}
}
