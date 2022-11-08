package com.example.session

import com.example.user.User
import org.ktorm.entity.Entity

interface Session: Entity<Session> {
    companion object: Entity.Factory<Session>()
    val id: Int
    val user: User
    val token: String
}