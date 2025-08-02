package com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub.handler

interface RedisMessageHandler {
    fun handle(message: String)
}
