package com.example.contact

import com.example.database
import com.example.message.MessageSchema
import com.example.user.UserSchema
import com.example.websocket.WebsocketsManager
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.*
import kotlin.math.max as mathMax
import kotlin.math.min as mathMin

// Ktor doesn't interpret String JSONs correctly
suspend fun ApplicationCall.receiveString(): String {
	val text = receive<String>()
	return text.drop(1).dropLast(1)
}

fun Route.contact() {
	route("/contact") {
		get {
			val principal = call.principal<UserIdPrincipal>()!!
			val userId = principal.name.toInt()

			val rows = database
				.from(UserSchema)
				.innerJoin(ContactSchema, on =
					(UserSchema.id eq ContactSchema.userId1) or (UserSchema.id eq ContactSchema.userId2)
				)
				.select(ContactSchema.id, ContactSchema.userId1, ContactSchema.userId2)
				.where {
					UserSchema.id eq userId
				}

			val contactBriefs = mutableListOf<ContactBrief>()
			for (row in rows) {
				val contactId = row[ContactSchema.id]!!
				val userId1 = row[ContactSchema.userId1]!!
				val userId2 = row[ContactSchema.userId2]!!
				val otherUserId = if (userId1 == userId) userId2 else userId1

				// Ktorm has some bugs with alias joining, therefore use different query (https://www.ktorm.org/en/joining.html)
				val name = database
					.from(UserSchema)
					.select(UserSchema.name)
					.where {
						UserSchema.id eq otherUserId
					}
					.iterator().next()[UserSchema.name]!!

				// Ktorm doesn't support nested joins
				val messageRows = database
					.from(MessageSchema)
					.select(MessageSchema.content, MessageSchema.date)
					.where {
						MessageSchema.contactId eq contactId
					}
					.orderBy(MessageSchema.id.desc())
					// .limit(1) not supported in SQLite
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
			val newContactUsername = call.receiveString()

			val rows = database
				.from(UserSchema)
				.select(UserSchema.id, UserSchema.name)
				.where {
					UserSchema.username eq newContactUsername
				}
				.iterator()

			if (!rows.hasNext()) {
				call.respond<ContactPostResponse>(ContactPostResponse("Username doesn't exists."))
				return@post
			}

			val row = rows.next()
			val newContactUserId = row[UserSchema.id]!!
			val newContactName = row[UserSchema.name]!!

			if (userId == newContactUserId) {
				call.respond(ContactPostResponse("""You can't add yourself 😬"""))
				return@post
			}

			val userId1 = mathMin(userId, newContactUserId)
			val userId2 = mathMax(userId, newContactUserId)
			val contactId = database.useTransaction {
				val recordCount = database
					.from(ContactSchema)
					.select(ContactSchema.id)
					.where {
						(ContactSchema.userId1 eq userId1) and (ContactSchema.userId2 eq userId2)
					}
					.totalRecords
				if (recordCount != 0) {
					call.respond(ContactPostResponse("User already in contacts."))
					return@post
				}

				database.insert(ContactSchema) {
					set(it.userId1, userId1)
					set(it.userId2, userId2)
				}

				database
					.from(ContactSchema)
					.select(ContactSchema.id)
					.where {
						(ContactSchema.userId1 eq userId1) and
						(ContactSchema.userId2 eq userId2)
					}
					.iterator()
					.next()[ContactSchema.id]!!
			}

			call.respond(ContactPostResponse(null))

			val name = database
				.from(UserSchema)
				.select(UserSchema.name)
				.where {
					UserSchema.id eq userId
				}
				.iterator()
				.next()[UserSchema.name]!!

			WebsocketsManager.notifyContact(newContactUserId, userId,
				ContactBrief(
					contactId,
					name,
					userId,
					"",
					"",
				), ContactBrief(
					contactId,
					newContactName,
					newContactUserId,
					"",
					"",
				)
			)
		}
	}
}
