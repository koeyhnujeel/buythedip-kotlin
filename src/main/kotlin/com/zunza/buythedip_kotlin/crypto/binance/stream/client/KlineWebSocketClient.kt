package com.zunza.buythedip_kotlin.crypto.binance.stream.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.buythedip_kotlin.crypto.binance.stream.manager.KlineStreamManager
import com.zunza.buythedip_kotlin.crypto.dto.KlineStreamResponse
import com.zunza.buythedip_kotlin.crypto.service.CryptoService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.WebSocketClient
import org.springframework.web.socket.handler.TextWebSocketHandler

private val logger = KotlinLogging.logger {  }

@Component
class KlineWebSocketClient(
    private val webSocketClient: WebSocketClient,
    private val objectMapper: ObjectMapper,
    private val klineStreamManager: KlineStreamManager,
    private val cryptoService: CryptoService
) : TextWebSocketHandler() {

    companion object {
        private const val URL = "wss://data-stream.binance.vision/stream"
    }

    @EventListener(ApplicationReadyEvent::class)
    fun connect() {
        try {
            webSocketClient.execute(this, URL)
            logger.info { "Binance WebSocket Connected.. Type: [KLINE]" }
        } catch (e: Exception) {
            logger.warn { e.message }
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        klineStreamManager.registerKlineSession(session)
    }

    override fun handleTextMessage(
        session: WebSocketSession,
        message: TextMessage
    ) {
        val klineStreamResponse = objectMapper.readValue(
            message.payload,
            KlineStreamResponse::class.java
            )

        val kline = klineStreamResponse.kline ?: return
        cryptoService.publishKlineData(kline.klineData!!)
    }
}
