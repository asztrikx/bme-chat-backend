package com.example.message

import com.example.contact.getOtherContactMember
import com.example.database
import com.example.websocket.WebsocketsManager
import org.ktorm.dsl.insert
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

suspend fun handleNewMessage(userId: Int, newMessage: NewMessage) {
	val otherUserId = getOtherContactMember(userId, newMessage.contactId) ?: return

	val datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
	val message = Message(
		newMessage.contactId,
		userId,
		otherUserId,
		newMessage.content,
		datetime,
	)

	database.insert(MessageSchema) {
		set(MessageSchema.content, message.content)
		set(MessageSchema.fromUserId, message.fromUserId)
		set(MessageSchema.contactId, message.contactId)
		set(MessageSchema.toUserId, message.toUserId)
		set(MessageSchema.date, message.date)
	}

	WebsocketsManager.notifyMessage(message)
}