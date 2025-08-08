package com.zunza.buythedip_kotlin.crypto.binance.stream.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.buythedip_kotlin.crypto.dto.SubscribeRequest
import com.zunza.buythedip_kotlin.crypto.dto.TradeStreamResponse
import com.zunza.buythedip_kotlin.crypto.repository.CryptoRepository
import com.zunza.buythedip_kotlin.crypto.service.CryptoMarketDataService
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.WebSocketClient
import org.springframework.web.socket.handler.TextWebSocketHandler

private val logger = KotlinLogging.logger {  }

@Component
class TradeWebSocketClient(
    private val webSocketClient: WebSocketClient,
    private val cryptoRepository: CryptoRepository,
    private val objectMapper: ObjectMapper,
    private val cryptoMarketDataService: CryptoMarketDataService
) : TextWebSocketHandler() {

    companion object {
        private const val URL = "wss://data-stream.binance.vision/stream"
        private const val STREAM_SUFFIX = "usdt@trade"
    }

    @EventListener(ApplicationReadyEvent::class)
    fun connect() {
        try {
            webSocketClient.execute(this, URL)
            logger.info { "Binance WebSocket Connected.. Type: [TRADE]" }
        } catch (e: Exception) {
            logger.warn { e.message }
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val symbols: List<String> = cryptoRepository.findAll()
            .map { crypto -> crypto.symbol.lowercase() + STREAM_SUFFIX }

        val payload = objectMapper.writeValueAsString(SubscribeRequest(
            "SUBSCRIBE",
            symbols,
            session.id
        ))

        session.sendMessage(TextMessage(payload))
    }

    override fun handleTextMessage(
        session: WebSocketSession,
        message: TextMessage
    ):Unit = runBlocking {
        val tradeStreamResponse = objectMapper.readValue(
            message.payload,
            TradeStreamResponse::class.java
        )

        val tradeData = tradeStreamResponse.tradeData ?: return@runBlocking

        launch { cryptoMarketDataService.accumulateMinuteTickerVolume(tradeData) }
        launch { cryptoMarketDataService.publishTopNTickerPrice(tradeData) }
        launch { cryptoMarketDataService.publishSingleTickerPrice(tradeData) }
    }
}
