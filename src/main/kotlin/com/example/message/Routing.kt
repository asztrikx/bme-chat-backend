package com.example.message

import com.example.database
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Route.message() {
	route("/message/{contactId}") {
		get {
			// TODO check privilege

			val contactId = call.parameters["contactId"]!!.toInt()

			val rows = database
				.from(MessageSchema)
				.select()
				.where {
					MessageSchema.contactId eq contactId
				}

			val messages = mutableListOf<Message>()
			for (row in rows) {
				messages += Message(
					row[MessageSchema.contactId]!!,
					row[MessageSchema.fromUserId]!!,
					row[MessageSchema.toUserId]!!,
					row[MessageSchema.content]!!,
					row[MessageSchema.date]!!,
				)
			}

			call.respond<List<Message>>(messages)
		}

		post {
			val principal = call.principal<UserIdPrincipal>()!!
			val userId = principal.name.toInt()

			val newMessage = call.receive<NewMessage>()
			val datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm"))

			database.insert(MessageSchema) {
				set(MessageSchema.content, newMessage.content)
				set(MessageSchema.fromUserId, userId)
				set(MessageSchema.contactId, newMessage.contactId)
				set(MessageSchema.toUserId, newMessage.otherUserId)
				set(MessageSchema.date, datetime)
			}
		}
	}
}