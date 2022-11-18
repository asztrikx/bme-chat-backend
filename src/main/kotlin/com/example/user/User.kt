package com.example.user

import com.example.session.SessionSchema
import org.ktorm.dsl.QueryRowSet

data class User (
    val id: Int,
    val username: String,
    var name: String,
    val passwordHash: String,
) {
    constructor(row: QueryRowSet): this(
        row[UserSchema.id]!!,
        row[UserSchema.username]!!,
        row[UserSchema.name]!!,
        row[UserSchema.passwordHash]!!,
    )
}
