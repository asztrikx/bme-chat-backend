package com.example.message

import com.example.user.UserSchema
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object MessageSchema : Table<Nothing>("message") {
    val id = int("id").primaryKey()
    val contactId = int("contactId")
    val fromUserId = int("fromUserId")
    val toUserId = int("toUserId")
    val content = varchar("content")
    val date = varchar("date")
}
