package com.zunza.buythedip_kotlin.crypto.dto

data class TopVolumeTickerSummaryResponse(
    val id: Long,
    val name: String,
    val symbol: String,
    val logo: String,
    val volume: Double
)
