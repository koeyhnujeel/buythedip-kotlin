package com.zunza.buythedip_kotlin.chat.entity

import jakarta.persistence.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "chat_messages")
class ChatMessage(
    @Id
    val id: String? = null,
    val userId: String,
    val sender: String,
    val content: String,
    val timestamp: Long
) {
    companion object {
        fun createOf(
            userId: String, sender: String, content: String, timestamp: String
        ): ChatMessage {
            return ChatMessage(
                userId = userId,
                sender = sender,
                content = content,
                timestamp = timestamp.toLong()
            )
        }
    }
}
