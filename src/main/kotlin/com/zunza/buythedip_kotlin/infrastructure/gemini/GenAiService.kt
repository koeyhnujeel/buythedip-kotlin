package com.zunza.buythedip_kotlin.infrastructure.gemini

import com.google.genai.Client
import org.springframework.stereotype.Service

/**
 * TODO: rate limit
 * 분당 30
 * 하루 200
 */

@Service
class GenAiService(
    private val genAiClient: Client,
) {
    companion object {
        private const val MODEL: String = "gemini-2.0-flash-lite"
    }

    fun generate(prompt: String): String {
        return genAiClient.models.generateContent(MODEL, prompt, null).text()!!
    }
}
