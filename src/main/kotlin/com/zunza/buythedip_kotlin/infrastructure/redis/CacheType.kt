package com.zunza.buythedip_kotlin.infrastructure.redis

import java.time.Duration

enum class CacheType(
    val cacheName: String,
    val ttl: Duration
) {
    NEWS_PAGE("NEWS:PAGE", Duration.ofMinutes(30)),
//    NEWS_DETAIL("NEWS:DETAIL:", Duration.ofMinutes(10))
}
