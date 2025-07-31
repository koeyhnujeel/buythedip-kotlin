package com.zunza.buythedip_kotlin.config

import com.zunza.buythedip_kotlin.infrastructure.redis.CacheType
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@EnableCaching
@Configuration
class RedisCacheConfig {

    @Bean
    fun cacheManager(redisConnectionFactory: RedisConnectionFactory): CacheManager {
        val defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer()))

        val cacheConfiguration: Map<String, RedisCacheConfiguration> = CacheType.entries
            .associate { cacheType ->
                cacheType.cacheName to defaultConfig.entryTtl(cacheType.ttl)
            }

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(defaultConfig.entryTtl(Duration.ofMinutes(1)))
            .withInitialCacheConfigurations(cacheConfiguration)
            .build()
    }
}
