package com.zunza.buythedip_kotlin.crypto.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class KlineStreamResponse(

    @JsonProperty("data")
    val kline: Kline?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Kline(

    @JsonProperty("k")
    val klineData: KlineData?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KlineData(

    @JsonProperty("t")
    val time: Long,

    @JsonProperty("s")
    val symbol: String,

    @JsonProperty("i")
    val interval: String,

    @JsonProperty("o")
    val open: Double,

    @JsonProperty("c")
    val close: Double,

    @JsonProperty("h")
    val high: Double,

    @JsonProperty("l")
    val low: Double,

    @JsonProperty("v")
    val volume: Double
)
