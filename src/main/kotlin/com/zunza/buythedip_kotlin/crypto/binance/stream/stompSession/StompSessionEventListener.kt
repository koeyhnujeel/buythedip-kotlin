package com.zunza.buythedip_kotlin.crypto.binance.stream.stompSession

import com.zunza.buythedip_kotlin.crypto.binance.stream.manager.KlineStreamManager
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.data.redis.connection.ReturnType
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import org.springframework.web.socket.messaging.SessionSubscribeEvent
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent

private val logger = KotlinLogging.logger {  }

/**
 * 페이지 이동 시: Unsubscribe -> Disconnect
 * 탭 종료 시 (강종): Disconnect
 */

@Component
class StompSessionEventListener(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val klineStreamManager: KlineStreamManager
) {
    companion object {
        private const val KLINE_TOPIC_PREFIX = "/topic/crypto/kline/"
    }

    @EventListener
    fun handleSessionSubscribe(event: SessionSubscribeEvent) {
        val accessor: StompHeaderAccessor = StompHeaderAccessor.wrap(event.message)
        val sessionId = accessor.sessionId ?: return
        val subscriptionId = accessor.subscriptionId ?: return
        val destination = accessor.destination ?: return
        val destinationKey = "$sessionId$subscriptionId"

        if (!destination.startsWith(KLINE_TOPIC_PREFIX)) return

        val count: Long = redisTemplate.opsForValue().increment(destination) ?: 0L
        redisTemplate.opsForValue().set(destinationKey, destination)
        redisTemplate.opsForSet().add(sessionId, destinationKey)

        if (count == 1L) {
            val(symbol, interval) = destination
                .removePrefix(KLINE_TOPIC_PREFIX)
                .split("/")

            klineStreamManager.subscribeKline(symbol.lowercase(), interval)
        }
    }

    @EventListener
    fun handleSessionUnsubscribe(event: SessionUnsubscribeEvent) {
        val accessor: StompHeaderAccessor = StompHeaderAccessor.wrap(event.message)
        val sessionId = accessor.sessionId ?: return
        val subscriptionId = accessor.subscriptionId ?: return
        val destinationKey = "$sessionId$subscriptionId"

        val destination = redisTemplate.opsForValue().get(destinationKey)
            ?.toString()
            ?: return

        if (!destination.startsWith(KLINE_TOPIC_PREFIX)) return

        safeDecrement(destination)
        redisTemplate.delete(destinationKey)
        redisTemplate.opsForSet().remove(sessionId, destinationKey)
    }

    @EventListener
    fun handleDisconnect(event: SessionDisconnectEvent) {
        val accessor: StompHeaderAccessor = StompHeaderAccessor.wrap(event.message)
        val sessionId = accessor.sessionId ?: return

        val destinationKeys = redisTemplate.opsForSet().members(sessionId) ?: return

        if (destinationKeys.isEmpty()) {
            redisTemplate.delete(sessionId)
        }

        for (destinationKey in destinationKeys) {
            val destination = redisTemplate.opsForValue().get(destinationKey).toString()
            safeDecrement(destination)
        }

        redisTemplate.delete(sessionId)
    }

    private fun safeDecrement(key: String): Long {
        val script = """
            local count = redis.call('GET', KEYS[1])
            count = tonumber(count) or 0
            if count > 0 then
                count = redis.call('DECR', KEYS[1])
            end
            return count
        """.trimIndent()

        return redisTemplate.execute<Long> { connection ->
            connection.scriptingCommands().eval(
                script.toByteArray(),
                ReturnType.INTEGER,
                1,
                key.toByteArray()
            ) as? Long
        } ?: 0L
    }
}
