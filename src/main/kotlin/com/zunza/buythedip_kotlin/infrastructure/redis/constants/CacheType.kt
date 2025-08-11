package com.zunza.buythedip_kotlin.infrastructure.redis.constants

import java.time.Duration

enum class CacheType(
    val cacheName: String,
    val ttl: Duration
) {
    NEWS_PAGE("NEWS:PAGE", Duration.ofMinutes(30)),
    NEWS_DETAIL("NEWS:DETAIL", Duration.ofMinutes(10)),
    CRYPTO_WITH_LOGO("CRYPTO:WITH:LOGO", Duration.ofMinutes(60)),
    CRYPTO_INFORMATION("CRYPTO:INFORMATION", Duration.ofMinutes(10))
}
