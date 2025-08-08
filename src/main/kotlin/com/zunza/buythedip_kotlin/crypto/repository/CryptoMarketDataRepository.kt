package com.zunza.buythedip_kotlin.crypto.repository

import com.zunza.buythedip_kotlin.infrastructure.redis.constants.RedisKey.*
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class CryptoMarketDataRepository(
    private val redisTemplate: RedisTemplate<String, Any>
) {
    fun saveOpenPrice(symbol: String, openPrice: Double) {
        redisTemplate.opsForValue().set(OPEN_PRICE_KEY_PREFIX.value + symbol, openPrice)
    }
}
