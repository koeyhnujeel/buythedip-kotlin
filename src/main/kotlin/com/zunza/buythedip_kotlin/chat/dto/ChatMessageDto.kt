package com.zunza.buythedip_kotlin.chat.dto

import java.time.LocalDateTime

data class ChatMessageDto(
    val sender: String,
    val content: String,
    val timestamp: Long
) {
    fun convertToMap(userId: Long): Map<String, String> {
        return mapOf(
            "userId" to userId.toString(),
            "sender" to this.sender,
            "content" to this.content,
            "timestamp" to this.timestamp.toString()
        )
    }
}

