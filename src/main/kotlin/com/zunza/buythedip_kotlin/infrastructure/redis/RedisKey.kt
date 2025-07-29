package com.zunza.buythedip_kotlin.infrastructure.redis

enum class RedisKey(
    val value: String
) {
    REFRESH_TOKEN_KEY_PREFIX("RT:");
}
