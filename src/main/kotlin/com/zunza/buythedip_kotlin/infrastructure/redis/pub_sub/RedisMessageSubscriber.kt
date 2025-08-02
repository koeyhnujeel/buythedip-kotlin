package com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub

import com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub.Channels.*
import com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub.handler.ChatHandler
import com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub.handler.RedisMessageHandler
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {  }

@Service
class RedisMessageSubscriber(
    private val chatHandler: ChatHandler
) {
    fun sendMessage(message: String, channel: String) {
        val handler = getHandler(channel)
        if (handler == null) {
            logger.warn { "Handler not found for channel: $channel" }
            return
        }

        handler.handle(message)
    }


    private fun getHandler(channel: String): RedisMessageHandler? {
        return when (channel) {
            CHAT_CHANNEL.topic -> chatHandler
            else -> null
        }
    }
}


