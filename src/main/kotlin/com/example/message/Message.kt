package com.example.message

import com.example.user.User
import org.ktorm.entity.Entity

interface Message: Entity<Message> {
    companion object: Entity.Factory<Message>()
    val id: Int
    val fromUser: User
    val toUser: User
    val content: String
    val date: String
}