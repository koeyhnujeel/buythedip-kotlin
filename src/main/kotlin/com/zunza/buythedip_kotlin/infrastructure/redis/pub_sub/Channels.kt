package com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub


enum class Channels(
    val topic: String,
) {
    CHAT_CHANNEL("chat:message")
}
