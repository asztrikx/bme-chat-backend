package com.example.websocket

import com.example.contact.ContactBrief
import com.example.message.Message
import io.ktor.server.websocket.*
import io.ktor.websocket.*

object WebsocketsManager {
	private val connections = mutableMapOf<Int, DefaultWebSocketServerSession>()

	suspend fun notifyContact(userId1: Int, userId2: Int, contactBrief1: ContactBrief, contactBrief2: ContactBrief) {
		send(userId1, contactBrief1)
		send(userId2, contactBrief2)
	}

	suspend fun notifyMessage(message: Message) {
		send(message.fromUserId, message)
		send(message.toUserId, message)
	}

	fun addConnection(userId:Int, session: DefaultWebSocketServerSession) {
		connections[userId] = session
	}

	suspend fun removeConnection(userId: Int) {
		connections[userId]?.close(CloseReason(1000, ""))
		connections.remove(userId)
	}

	private suspend inline fun <reified T> send(userId: Int, t: T) {
		try {
			connections[userId]?.sendSerialized(t)
		} catch (e: Exception) {
			removeConnection(userId)
		}
	}
}