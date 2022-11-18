package com.example.contact

data class ContactBrief (
	val id: Int,
	val name: String,
	val userId: Int,
	val lastMessageContent: String,
	val lastMessageDate: String,
)