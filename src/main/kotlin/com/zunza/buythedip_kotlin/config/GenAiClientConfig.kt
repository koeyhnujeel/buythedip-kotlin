package com.zunza.buythedip_kotlin.config

import com.google.genai.Client
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GenAiClientConfig(

    @Value("\${gemini.key}")
    private val key: String
) {
    @Bean
    fun genAiClient(): Client =
        Client.builder()
            .apiKey(key)
            .build()
}
