package com.example.contact

import com.example.database
import com.example.login.LoginRequest
import com.example.message.MessageSchema
import com.example.user.UserSchema
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.ktorm.dsl.*

// Ktor doesn't interpret String JSONs correctly
suspend fun ApplicationCall.receiveString(): String {
	val text = receive<String>()
	return text.drop(1).dropLast(1) // TODO oneliner doesnt work
}

fun Route.contact() {
	route("/contact") {
		get {
			val principal = call.principal<UserIdPrincipal>()!!
			val userId = principal.name.toInt()

			val rows = database
				.from(ContactSchema)
				.innerJoin(UserSchema, on = UserSchema.id eq ContactSchema.userId2)
				.select(ContactSchema.id, ContactSchema.userId2, UserSchema.name)
				.where {
					ContactSchema.userId1 eq userId
				}

			val contactBriefs = mutableListOf<ContactBrief>()
			for (row in rows) {
				val contactId = row[ContactSchema.id]!!
				val otherUserId = row[ContactSchema.userId2]!!
				val name = row[UserSchema.name]!!

				// Ktorm doesn't support nested joins
				val messageRows = database
					.from(MessageSchema)
					.select(MessageSchema.content, MessageSchema.date)
					.where {
						MessageSchema.contactId eq contactId
					}
					.orderBy(MessageSchema.id.desc())
					//limit not supported
					.iterator()

				contactBriefs += if (!messageRows.hasNext()) {
					ContactBrief(contactId, name, otherUserId, "", "")
				} else {
					val messageRow = messageRows.next()
					val lastMessageContent = messageRow[MessageSchema.content]!!
					val lastMessageDate = messageRow[MessageSchema.date]!!
					ContactBrief(contactId, name, otherUserId, lastMessageContent, lastMessageDate)
				}
			}

			call.respond<List<ContactBrief>>(contactBriefs)
		}
		post {
			val principal = call.principal<UserIdPrincipal>()!!
			val userId = principal.name.toInt()
			val username = call.receiveString()

			val rows = database
				.from(UserSchema)
				.select(UserSchema.id)
				.where {
					UserSchema.username eq username
				}
				.iterator()

			if (!rows.hasNext()) {
				call.respond(ContactPostResponse("Username doesn't exists.", null))
				return@post
			}

			val row = rows.next()
			val newContactUserId = row[UserSchema.id]!!

			if (userId == newContactUserId) {
				call.respond(ContactPostResponse("""You can't add yourself 😬""", null))
				return@post
			}

			database.useTransaction {
				val recordCount = database
					.from(ContactSchema)
					.select(ContactSchema.id)
					.where {
						(ContactSchema.userId1 eq userId) and (ContactSchema.userId2 eq newContactUserId)
					}
					.totalRecords
				if (recordCount != 0) {
					call.respond(ContactPostResponse("User already in contacts.", null))
					return@post
				}

				database.batchInsert(ContactSchema) {
					item {
						set(it.userId1, userId)
						set(it.userId2, newContactUserId)
					}
				}
			}

			call.respond(ContactPostResponse(null, ContactBrief(

			)))
		}
	}
}