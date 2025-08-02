package com.zunza.buythedip_kotlin.infrastructure.redis.stream

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.connection.stream.Consumer
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.connection.stream.ReadOffset
import org.springframework.data.redis.connection.stream.RecordId
import org.springframework.data.redis.connection.stream.StreamOffset
import org.springframework.data.redis.connection.stream.StreamReadOptions
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {  }

@Service
class RedisStreamService(
    private val redisTemplate: RedisTemplate<String, Any>
) {
    fun addToStream(key: String, fields: Map<String, String>) {
        redisTemplate.opsForStream<String, String>().add(key, fields)
    }

    fun createGroup(key: String, group: String) {
        redisTemplate.opsForStream<String, String>()
            .createGroup(key, ReadOffset.from("0"), group)
    }

    fun read(
        group: String,
        consumer: String,
        readOptions: StreamReadOptions,
        key: String
    ): List<MapRecord<String, String, String>> {
        return redisTemplate.opsForStream<String, String>().read(
            Consumer.from(group, consumer),
            readOptions,
            StreamOffset.create(key, ReadOffset.lastConsumed())
        ) ?: emptyList()
    }


    fun ack(key: String, group: String, vararg recordIds: RecordId) {
        redisTemplate.opsForStream<String, String>().acknowledge(key, group, *recordIds)
    }
}
