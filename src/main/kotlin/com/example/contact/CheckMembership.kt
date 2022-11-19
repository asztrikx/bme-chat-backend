package com.example.contact

import com.example.database
import org.ktorm.dsl.*

fun getOtherContactMember(userId: Int, contactId: Int): Int? {
	val rows = database
		.from(ContactSchema)
		.select(ContactSchema.userId1, ContactSchema.userId2)
		.where {
			ContactSchema.id eq contactId and ((ContactSchema.userId1 eq userId) or (ContactSchema.userId2 eq userId))
		}
		.iterator()

	if (!rows.hasNext()) {
		return null
	}

	val row = rows.next()
	val userId1 = row[ContactSchema.userId1]!!
	val userId2 = row[ContactSchema.userId2]!!

	return if (userId1 == userId) {
		userId2
	} else {
		userId1
	}
}