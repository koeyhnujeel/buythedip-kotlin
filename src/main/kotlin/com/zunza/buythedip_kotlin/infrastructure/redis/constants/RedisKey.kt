package com.zunza.buythedip_kotlin.infrastructure.redis.constants

enum class RedisKey(
    val value: String
) {
    REFRESH_TOKEN_KEY_PREFIX("RT:"),
    CHAT_MESSAGE_STREAM_KEY("CHAT:STREAM"),
    OPEN_PRICE_KEY_PREFIX("OPEN:PRICE:"),
    MINUTE_BUCKET_KEY_PREFIX("tv:"),
    AGGREGATED_VOLUME_KEY(MINUTE_BUCKET_KEY_PREFIX.value + "AGGREGATED"),
    TOP_VOLUME_SYMBOLS_KEY("TOP:VOLUME:SYMBOLS")
}
