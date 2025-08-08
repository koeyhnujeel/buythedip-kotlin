package com.zunza.buythedip_kotlin.crypto.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class TradeStreamResponse(

    @JsonProperty("data")
    val tradeData: TradeData?
)

@JsonIgnoreProperties(ignoreUnknown = true)
class TradeData(

    @JsonProperty("s")
    val symbol: String,

    @JsonProperty("p")
    val price: Double,

    @JsonProperty("q")
    val quantity: Double,

    @JsonProperty("T")
    val tradeTime: Long
)
