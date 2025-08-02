package com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.buythedip_kotlin.chat.dto.ChatMessageDto
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component

@Component
class ChatHandler(
    private val objectMapper: ObjectMapper,
    private val messagingTemplate: SimpMessageSendingOperations
) : RedisMessageHandler {

    override fun handle(message: String) {
        val chatMessageDto = objectMapper.readValue(message, ChatMessageDto::class.java)
        messagingTemplate.convertAndSend("/topic/chat/room/public", chatMessageDto)
    }
}
