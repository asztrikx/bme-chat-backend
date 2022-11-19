package com.example.message

import com.example.contact.getOtherContactMember
import com.example.database
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.*

fun Route.message() {
	route("/message/{contactId}") {
		get {
			val principal = call.principal<UserIdPrincipal>()!!
			val userId = principal.name.toInt()
			val contactId = call.parameters["contactId"]!!.toInt()

			getOtherContactMember(userId, contactId) ?: return@get

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
	}
}