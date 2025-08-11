package com.zunza.buythedip_kotlin.crypto.dto

data class CryptoInformationResponse(
    val name: String = "",
    val symbol: String = "",
    val logo: String = "",
    val description: String = "",
    val website: List<String> = emptyList(),
    val twitter: List<String> = emptyList(),
    val explorer: List<String> = emptyList(),
    val tagNames: List<String> = emptyList(),
)
