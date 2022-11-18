package com.example.session

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object SessionSchema: Table<Nothing>("session") {
    val id = int("id").primaryKey()
    val userId = int("userId")
    val token = varchar("token")
}
