package com.example.message

import com.example.user.User

data class Message (
    val contactId: Int,
    val fromUserId: Int,
    val toUserId: Int,
    val content: String,
    val date: String,
)