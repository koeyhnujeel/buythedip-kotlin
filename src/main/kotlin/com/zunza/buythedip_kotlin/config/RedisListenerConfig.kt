package com.zunza.buythedip_kotlin.config

import com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub.Channels
import com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub.RedisMessageSubscriber
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter

@Configuration
class RedisListenerConfig(
    private val subscriber: RedisMessageSubscriber,
    private val redisConnectionFactory: RedisConnectionFactory
) {
    @Bean
    fun chatChannelTopic(): ChannelTopic {
        return ChannelTopic(Channels.CHAT_CHANNEL.topic)
    }

    @Bean
    fun listenerAdapter(): MessageListenerAdapter {
        return MessageListenerAdapter(subscriber, "sendMessage")
    }

    @Bean
    fun container(): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(redisConnectionFactory)
        container.addMessageListener(listenerAdapter(), chatChannelTopic())
        return container
    }
}
