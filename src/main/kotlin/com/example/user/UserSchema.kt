package com.example.user

import com.example.message.MessageSchema
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object UserSchema: Table<Nothing>("user") {
    val id = int("id").primaryKey()
    val username = varchar("username")
    val name = varchar("name")
    val passwordHash = varchar("passwordHash")
}
