package com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub.handler

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component

@Component
class TopVolumeTickerSummaryHandler(
    private val objectMapper: ObjectMapper,
    private val messagingTemplate: SimpMessageSendingOperations
) : RedisMessageHandler {

    override fun handle(message: String) {
        val response = objectMapper.readValue(message, List::class.java)
        messagingTemplate.convertAndSend("/topic/crypto/top/volume/summary", response)
    }
}
