package com.example.contact

import org.ktorm.schema.Table
import org.ktorm.schema.int

object ContactSchema: Table<Nothing>("contact") {
    val id = int("id").primaryKey()
    val userId1 = int("userId1")
    val userId2 = int("userId2")
}
