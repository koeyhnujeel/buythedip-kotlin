package com.zunza.buythedip_kotlin.crypto.dto

data class TopVolumeTickerPriceResponse(
    val symbol: String,
    val currentPrice: Double,
    val changeRate: Double
)
