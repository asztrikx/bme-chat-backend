package com.example.register

import com.example.database
import com.example.session.*
import com.example.user.UserSchema
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.*
import org.mindrot.jbcrypt.BCrypt

fun Route.register() {
	post("/register") {
		println(call.request.headers)
		val registerRequest = call.receive<RegisterRequest>()

		// do not run in transaction to avoid errors
		val passwordHash = BCrypt.hashpw(registerRequest.password, BCrypt.gensalt());

		database.useTransaction {
			// username collision
			val numOfRecords = database
				.from(UserSchema)
				.select()
				.where {
					UserSchema.username eq registerRequest.username
				}
				.totalRecords

			if (numOfRecords != 0) {
				call.respond<RegisterResponse>(RegisterResponse("Username already taken."))
				return@post
			}

			database.insert(UserSchema) {
				set(it.name, registerRequest.name)
				set(it.username, registerRequest.username)
				set(it.passwordHash, passwordHash)
			}
		}

		call.respond<RegisterResponse>(RegisterResponse(null))
	}
}