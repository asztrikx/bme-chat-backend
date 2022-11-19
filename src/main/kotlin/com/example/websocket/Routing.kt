package com.example.websocket

import com.example.contact.getOtherContactMember
import com.example.database
import com.example.message.Message
import com.example.message.MessageSchema
import com.example.message.NewMessage
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.ktorm.dsl.insert
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
					handleMessage(userId, newMessage)
				}
			} catch (_: Exception) {} finally {
				WebsocketsManager.removeConnection(userId)
			}
		}
 	}
}

suspend fun handleMessage(userId: Int, newMessage: NewMessage) {
	val otherUserId = getOtherContactMember(userId, newMessage.contactId) ?: return

	val datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm"))
	val message = Message(
		newMessage.contactId,
		userId,
		otherUserId,
		newMessage.content,
		datetime,
	)

	database.insert(MessageSchema) {
		set(MessageSchema.content, message.content)
		set(MessageSchema.fromUserId, message.fromUserId)
		set(MessageSchema.contactId, message.contactId)
		set(MessageSchema.toUserId, message.toUserId)
		set(MessageSchema.date, message.date)
	}

	WebsocketsManager.notifyMessage(message)
}