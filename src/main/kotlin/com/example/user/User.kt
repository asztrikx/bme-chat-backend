package com.example.user

import org.ktorm.entity.Entity

interface User: Entity<User> {
    companion object : Entity.Factory<User>()

    val id: Int
    val username: String
    var name: String
    val passwordHash: String
}
