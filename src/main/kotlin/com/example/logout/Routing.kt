package com.example.logout

import com.example.database
import com.example.session.SessionSchema
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.from

fun Route.logout() {
	get("/logout") {
		val principal = call.principal<UserIdPrincipal>()!!
		val userId = principal.name.toInt()

		database
			.delete(SessionSchema) {
				SessionSchema.userId eq userId
			}

		call.respond<Unit>(TODO())
	}
}