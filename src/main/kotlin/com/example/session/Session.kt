package com.example.session

import com.example.database
import org.ktorm.dsl.*
import java.security.SecureRandom
import java.util.*

data class Session (
    val id: Int,
    val userId: Int,
    val token: String,
) {
    constructor(row: QueryRowSet): this(
        row[SessionSchema.id]!!,
        row[SessionSchema.userId]!!,
        row[SessionSchema.token]!!,
    )
    companion object
}

const val TOKEN_SIZE = 256

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

fun Session.Companion.validate(userId: Int, tokenCandidate: String): Boolean {
    // To avoid timing attack we can't filter for token
    val rows = database
        .from(SessionSchema)
        .select(SessionSchema.token)
        .where {
            SessionSchema.userId eq userId
        }

    for (row in rows) {
        val token = row[SessionSchema.token]!!
        // timing attack safe compare
        if (token.secureCompare(tokenCandidate)) {
            return true
        }
    }
    return false
}
