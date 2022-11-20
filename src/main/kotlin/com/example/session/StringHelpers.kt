package com.example.session

import io.ktor.server.application.*
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.*

fun String.Companion.randomBase64(size: Int): String {
	val bytes = ByteArray(size)
	SecureRandom.getInstanceStrong().nextBytes(bytes)
	return Base64.getEncoder().encodeToString(bytes)
}

fun String.secureCompare(other: String): Boolean {
	if (length != other.length) {
		return false
	}

	var equals = true
	for (i in 0 until length) {
		// and: do not short circuit
		equals = equals and (get(i) == other.get(i))
	}
	return equals
}

fun String.base64Decode(): String {
	return String(Base64.getDecoder().decode(this), StandardCharsets.UTF_8)
}

fun ApplicationCall.getAuthorization(): Pair<Int, String>? {
	val headerValue = request.headers["Authorization"]
	headerValue ?: return null

	val authorizationDecoded = headerValue.split(" ")[1]
	val authorization = authorizationDecoded.base64Decode().split(":")
	return Pair(authorization[0].toInt(), authorization[1].base64Decode())
}