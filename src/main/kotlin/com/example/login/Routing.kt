package com.example.login

import com.example.database
import com.example.session.*
import com.example.user.UserSchema
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.*
import org.mindrot.jbcrypt.BCrypt

fun Route.login() {
	post("/login") {
		val loginRequest = call.receive<LoginRequest>()

		// get id, pwHash
		val iterator = database
			.from(UserSchema)
			.select(UserSchema.id, UserSchema.passwordHash)
			.where {
				UserSchema.username eq loginRequest.username
			}
			.iterator()

		if (!iterator.hasNext()) {
			// Retrofit doesn't support null response
			call.respond<LoginResponse>(LoginResponse(
				0,
				"",
			))
			return@post
		}
		val row = iterator.next()

		// validate pw
		val userId = row[UserSchema.id]!!
		val passwordHash = row[UserSchema.passwordHash]!!

		if (!BCrypt.checkpw(loginRequest.password, passwordHash)) {
			call.respond<LoginResponse>(LoginResponse(
				0,
				"",
			))
			return@post
		}

		// add session
		val token = String.randomBase64(TOKEN_SIZE)
		database.insert(SessionSchema) {
			set(it.userId, userId)
			set(it.token, token)
		}

		call.respond(LoginResponse(
			userId,
			token,
		))
	}
}