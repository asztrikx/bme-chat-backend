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
    companion object
}

const val TOKEN_SIZE = 256

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
