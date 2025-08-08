package com.zunza.buythedip_kotlin.chat.service

import com.zunza.buythedip_kotlin.chat.dto.ChatMessageDto
import com.zunza.buythedip_kotlin.infrastructure.redis.constants.RedisKey.*
import com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub.Channels
import com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub.RedisMessagePublisher
import com.zunza.buythedip_kotlin.infrastructure.redis.stream.RedisStreamService
import org.springframework.stereotype.Service

@Service
class ChatService(
    private val redisMessagePublisher: RedisMessagePublisher,
    private val redisStreamService: RedisStreamService
) {
    fun sendMessage(userId: Long, chatMessage: ChatMessageDto) {
        redisMessagePublisher.publishMessage(Channels.CHAT_CHANNEL.topic, chatMessage)
        redisStreamService.addToStream(CHAT_MESSAGE_STREAM_KEY.value, chatMessage.convertToMap(userId))
    }
}
