package com.example.logout

import com.example.database
import com.example.session.SessionSchema
import com.example.session.getAuthorization
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.and
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq

fun Route.logout() {
	post("/logout") {
		val principal = call.principal<UserIdPrincipal>()!!
		val userId = principal.name.toInt()

		val token = call.getAuthorization()!!.second

		database
			.delete(SessionSchema) {
				(SessionSchema.userId eq userId) and (SessionSchema.token eq token)
			}

		call.respondText("\"\"")
	}
}