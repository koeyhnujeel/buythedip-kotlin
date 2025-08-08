package com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.buythedip_kotlin.crypto.dto.TopVolumeTickerPriceResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {  }

@Component
class TopVolumeTickerPriceHandler(
    private val objectMapper: ObjectMapper,
    private val messagingTemplate: SimpMessageSendingOperations
) : RedisMessageHandler {

    override fun handle(message: String) {
        val response = objectMapper.readValue(message, TopVolumeTickerPriceResponse::class.java)
        messagingTemplate.convertAndSend("/topic/crypto/top/volume/price", response)
    }
}
