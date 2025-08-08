package com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub

import com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub.Channels.*
import com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub.handler.ChatHandler
import com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub.handler.RedisMessageHandler
import com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub.handler.TopVolumeTickerPriceHandler
import com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub.handler.TopVolumeTickerSummaryHandler
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {  }

@Service
class RedisMessageSubscriber(
    private val chatHandler: ChatHandler,
    private val topVolumeTickerSummaryHandler: TopVolumeTickerSummaryHandler,
    private val topVolumeTickerPriceHandler: TopVolumeTickerPriceHandler
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
            TOP_VOLUME_TICKER_SUMMARY_CHANNEL.topic -> topVolumeTickerSummaryHandler
            TOP_VOLUME_TICKER_PRICE_CHANNEL.topic -> topVolumeTickerPriceHandler
            else -> null
        }
    }
}


