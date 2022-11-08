package com.example.message

import com.example.user.UserSchema
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object MessageSchema : Table<Message>("message") {
    val id = int("id").primaryKey().bindTo { it.id }
    val fromUserId = int("fromUserId").references(UserSchema) { it.fromUser }
    val toUserId = int("fromUserId").references(UserSchema) { it.toUser }
    val content = varchar("content").bindTo { it.content }
    val date = varchar("date").bindTo { it.date }
}

val Database.messages get() = this.sequenceOf(MessageSchema)