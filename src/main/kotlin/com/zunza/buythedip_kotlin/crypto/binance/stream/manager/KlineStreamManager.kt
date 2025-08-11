package com.zunza.buythedip_kotlin.crypto.binance.stream.manager

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.buythedip_kotlin.crypto.dto.SubscribeRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

private val logger = KotlinLogging.logger {  }

@Component
class KlineStreamManager(
    private val objectMapper: ObjectMapper,
) {
    companion object {
        private const val SYMBOL_SUFFIX = "usdt"
        private const val INTERVAL_PREFIX = "@kline_"
    }

    private var currentSession: WebSocketSession? = null

    fun registerKlineSession(session: WebSocketSession) {
        this.currentSession = session
    }

    fun subscribeKline(symbol: String, interval: String) {
        val params = symbol + SYMBOL_SUFFIX + INTERVAL_PREFIX + interval
        sendMessage("SUBSCRIBE", listOf(params))
    }

    fun unsubscribeCombinedKline(params: List<String>) {
        sendMessage("UNSUBSCRIBE", params)
    }

    fun unsubscribeKline(symbol: String, interval: String) {
        val params = symbol + SYMBOL_SUFFIX + INTERVAL_PREFIX + interval
        sendMessage("UNSUBSCRIBE", listOf(params))
    }

    fun sendMessage(method: String, params: List<String>) {
        try {
            val payload = objectMapper.writeValueAsString(
                SubscribeRequest(
                    method,
                    params,
                    this.currentSession?.id.toString()
                )
            )
            this.currentSession?.sendMessage(TextMessage(payload))
        } catch (e: Exception) {
            throw e
        }
    }
}
