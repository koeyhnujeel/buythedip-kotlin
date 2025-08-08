package com.zunza.buythedip_kotlin.crypto.dto

data class SubscribeRequest(
    val method: String,
    val params: List<String>,
    val id: String
)
