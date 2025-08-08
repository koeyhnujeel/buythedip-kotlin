package com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub


enum class Channels(
    val topic: String,
) {
    CHAT_CHANNEL("chat:message"),
    TOP_VOLUME_TICKER_SUMMARY_CHANNEL("top:volume:ticker:summary"),
    TOP_VOLUME_TICKER_PRICE_CHANNEL("top:volume:ticker:price")
}
