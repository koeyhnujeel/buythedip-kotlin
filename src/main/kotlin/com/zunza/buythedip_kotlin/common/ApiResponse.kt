package com.zunza.buythedip_kotlin.common

import com.fasterxml.jackson.annotation.JsonInclude

data class ApiResponse<T> (
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val data: T,
    val code: Int,
    val success: Boolean = code >= 200 && code < 300
)
