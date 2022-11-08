package com.example.user

import com.example.message.MessageSchema
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object UserSchema: Table<User>("user") {
    val id = int("id").primaryKey().bindTo { it.id }
    val username = varchar("username").bindTo { it.username }
    val name = varchar("name").bindTo { it.name }
    val passwordHash = varchar("passwordHash").bindTo { it.passwordHash }
}

val Database.users get() = this.sequenceOf(UserSchema)