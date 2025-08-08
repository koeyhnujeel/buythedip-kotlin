package com.zunza.buythedip_kotlin.chat.service

import com.zunza.buythedip_kotlin.chat.entity.ChatMessage
import com.zunza.buythedip_kotlin.infrastructure.redis.constants.RedisKey.*
import com.zunza.buythedip_kotlin.infrastructure.redis.stream.RedisStreamService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.connection.stream.RecordId
import org.springframework.data.redis.connection.stream.StreamReadOptions
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*

private val logger = KotlinLogging.logger {  }

@Service
class ChatMessageStreamConsumer(
    private val mongoTemplate: MongoTemplate,
    private val redisStreamService: RedisStreamService
) {
    companion object {
        private const val GROUP_NAME = "CHAT-GROUP"
        private val CONSUMER_NAME: String = "server-${UUID.randomUUID()}"
    }

    @PostConstruct
    fun init() {
        try {
            redisStreamService.createGroup(CHAT_MESSAGE_STREAM_KEY.value, GROUP_NAME)
        } catch (e: Exception) {
            logger.info { "Consumer group $GROUP_NAME already exists." }
        }
    }

    fun consumeAndStore() {
        val readOptions = StreamReadOptions.empty()
            .count(200)
            .block(Duration.ofSeconds(2L))

        val mapRecords: List<MapRecord<String, String, String>> =  redisStreamService.read(
            GROUP_NAME,
            CONSUMER_NAME,
            readOptions,
            CHAT_MESSAGE_STREAM_KEY.value
        )

        if (mapRecords.isEmpty()) return

        mongoTemplate.insertAll(convertToDocuments(mapRecords))
        redisStreamService.ack(
                CHAT_MESSAGE_STREAM_KEY.value,
                GROUP_NAME,
                *extractRecordIds(mapRecords))
    }

    private fun extractRecordIds(mapRecords: List<MapRecord<String, String, String>>): Array<RecordId> {
        return mapRecords.map { it.id }
            .toTypedArray()
    }


    private fun convertToDocuments(messages: List<MapRecord<String, String, String>>): List<ChatMessage> {
        return messages.map { message ->
            val value: Map<String, String> = message.value
            ChatMessage.createOf(
                value["userId"]!!,
                value["sender"]!!,
                value["content"]!!,
                value["timestamp"]!!
            )
        }
    }
}
