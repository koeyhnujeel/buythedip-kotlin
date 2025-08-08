package com.zunza.buythedip_kotlin.crypto.repository

import com.zunza.buythedip_kotlin.infrastructure.redis.constants.RedisKey.*
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class CryptoMarketDataRepository(
    private val redisTemplate: RedisTemplate<String, Any>
) {
    fun saveOpenPrice(symbol: String, openPrice: Double) {
        redisTemplate.opsForValue().set(OPEN_PRICE_KEY_PREFIX.value + symbol, openPrice)
    }

    fun findOpenPriceBySymbol(symbol: String): Double {
        return redisTemplate.opsForValue().get(OPEN_PRICE_KEY_PREFIX.value + symbol) as? Double ?: 0.0
    }

    fun saveVolumeByMinute(key: String, symbol: String, volume: Double, ttl: Long) {
        redisTemplate.opsForZSet().incrementScore(key, symbol, volume)
        redisTemplate.expire(key, Duration.ofMinutes(ttl))
    }

    fun aggregateVolume(keys: List<String>, targetKey: String) {
        redisTemplate.opsForZSet().unionAndStore(keys.first(), keys.drop(1), targetKey)
    }

    fun findTopNTickers(n: Long): Set<ZSetOperations.TypedTuple<Any>>? {
        return redisTemplate.opsForZSet().reverseRangeWithScores(
            AGGREGATED_VOLUME_KEY.value,
            0,
            n - 1)
    }

    fun updateTopVolumeSymbols(symbols: Set<String>) {
        redisTemplate.opsForValue().set(TOP_VOLUME_SYMBOLS_KEY.value, symbols)
    }

    fun findTopVolumeSymbols(): Set<String> {
        val result = redisTemplate.opsForValue().get(TOP_VOLUME_SYMBOLS_KEY.value)
        return result as? Set<String> ?: emptySet()
    }
}
