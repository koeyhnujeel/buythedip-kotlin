package com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.buythedip_kotlin.crypto.dto.SingleTickerPriceResponse
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component


@Component
class SingleTickerPriceHandler(
    private val objectMapper: ObjectMapper,
    private val messagingTemplate: SimpMessageSendingOperations
) : RedisMessageHandler {

    override fun handle(message: String) {
        val response = objectMapper.readValue(message, SingleTickerPriceResponse::class.java)
        messagingTemplate.convertAndSend("/topic/crypto/price/${response.symbol}", response)
    }
}
