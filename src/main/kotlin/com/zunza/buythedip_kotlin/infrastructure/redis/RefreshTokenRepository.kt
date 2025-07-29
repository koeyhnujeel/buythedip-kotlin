package com.zunza.buythedip_kotlin.infrastructure.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RefreshTokenRepository(
    private val redisTemplate: RedisTemplate<String, Any>,

    @Value("\${jwt.refresh-token-expire-time}")
    private val ttl: Long
) {
    fun save(userId: Long, refreshToken: String) = redisTemplate.opsForValue()
        .set(getKey(userId), refreshToken, ttl, TimeUnit.MILLISECONDS)

    fun get(userId: Long): String? = redisTemplate.opsForValue().get(getKey(userId)).toString()

    fun delete(userId: Long) = redisTemplate.delete(getKey(userId))

    private fun getKey(userId: Long): String = RedisKey.REFRESH_TOKEN_KEY_PREFIX.value + userId
}

