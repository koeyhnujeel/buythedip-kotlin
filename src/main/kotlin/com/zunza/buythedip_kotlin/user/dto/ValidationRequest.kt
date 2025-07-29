package com.zunza.buythedip_kotlin.user.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

data class ValidationRequest(
    val validationType: ValidationType,
    val value: String
) {
    enum class ValidationType(
        @JsonValue
        val fieldName: String
    ) {
        ACCOUNT_ID("accountId"),
        NICKNAME("nickname");

        @JsonCreator
        fun deserialize(fieldName: String): ValidationType? {
            for (type in ValidationType.entries) {
                if (fieldName == type.fieldName) {
                    return type
                }
            }
            return null
        }
    }
}
