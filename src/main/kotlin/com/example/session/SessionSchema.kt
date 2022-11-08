package com.example.session

import com.example.message.MessageSchema
import com.example.user.UserSchema
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object SessionSchema: Table<Session>("session") {
    val id = int("id").primaryKey().bindTo { it.id }
    val userId = int("userId").references(UserSchema) { it.user }
    val token = varchar("token").bindTo { it.token }
}

val Database.sessions get() = this.sequenceOf(SessionSchema)