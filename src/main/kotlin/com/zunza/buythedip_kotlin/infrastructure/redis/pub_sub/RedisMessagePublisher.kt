package com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RedisMessagePublisher(
    private val redisTemplate: RedisTemplate<String, Any>
) {
    fun publishMessage(topic: String, message: Any) {
        redisTemplate.convertAndSend(topic, message)
    }
}
