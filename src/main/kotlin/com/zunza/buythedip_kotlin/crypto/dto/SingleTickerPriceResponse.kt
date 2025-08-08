package com.zunza.buythedip_kotlin.crypto.dto

data class SingleTickerPriceResponse(
    val symbol: String,
    val currentPrice: Double,
    val changePrice: Double,
    val changeRate: Double
)
