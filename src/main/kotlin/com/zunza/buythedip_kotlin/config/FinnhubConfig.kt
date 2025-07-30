package com.zunza.buythedip_kotlin.config

import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.infrastructure.ApiClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FinnhubConfig(

    @Value("\${finnhub.key}")
    private val key: String
) {
    @Bean
    fun finnhubClient(): DefaultApi {
        ApiClient.apiKey["token"] = key
        return DefaultApi()
    }
}
