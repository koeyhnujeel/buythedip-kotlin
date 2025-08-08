package com.zunza.buythedip_kotlin.crypto.converter

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class StringListConverter(
    private val objectMapper: ObjectMapper
) : AttributeConverter<List<String>, String> {
    override fun convertToDatabaseColumn(p0: List<String>?): String? {
        if (p0 == null || p0.isEmpty()) {
            return null
        }

        try {
            return objectMapper.writeValueAsString(p0)
        } catch (e: JsonProcessingException) {
            throw RuntimeException("List to JSON conversion failed", e)
        }
    }

    override fun convertToEntityAttribute(p0: String?): List<String>? {
        if (p0 == null || p0.isEmpty()) {
            return emptyList()
        }

        try {
            return objectMapper.readValue(p0, object : TypeReference<List<String>>() {})
        } catch (e: JsonProcessingException) {
            throw RuntimeException("JSON to List conversion failed", e)
        }
    }
}

