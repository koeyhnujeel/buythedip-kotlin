package com.zunza.buythedip_kotlin.crypto.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonNode

data class KlineResponse(
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val symbol: String = "",
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val interval: String = "",
    val candle: CandleData,
    val volume: VolumeData
) {
    companion object {
        fun createHistoricalKlineResponse(node: JsonNode): KlineResponse {
            return KlineResponse(
                candle = CandleData.createHistoricalCandleData(node),
                volume = VolumeData.createHistoricalVolumeData(node)
            )
        }

        fun createRealtimeKlineResponse(klineData: KlineData): KlineResponse {
            return KlineResponse(
                klineData.symbol.removeSuffix("USDT"),
                klineData.interval,
                CandleData.createRealtimeCandleData(klineData),
                VolumeData.createRealtimeVolumeData(klineData)
            )
        }
    }

    data class CandleData(
        val time: Long,
        val open: Double,
        val high: Double,
        val low: Double,
        val close: Double,
    ) {
        companion object {
            fun createHistoricalCandleData(node: JsonNode): CandleData {
                return CandleData(
                    node[0].asLong() / 1_000,
                    node[1].asDouble(),
                    node[2].asDouble(),
                    node[3].asDouble(),
                    node[4].asDouble()
                )
            }

            fun createRealtimeCandleData(klineData: KlineData): CandleData {
                return CandleData(
                    klineData.time / 1_000,
                    klineData.open,
                    klineData.high,
                    klineData.low,
                    klineData.close
                )
            }
        }
    }

    data class VolumeData(
        val time: Long,
        val value: Double,
        val color: String
    ) {
        companion object {
            private const val COLOR_BULLISH = "rgba(244, 67, 54, 1.0)"
            private const val COLOR_BEARISH = "rgba(76, 175, 80, 1.0)"

            fun createHistoricalVolumeData(node: JsonNode): VolumeData {
                return VolumeData(
                    node[0].asLong() / 1_000,
                    node[5].asDouble(),
                    if (node[1].asDouble() >= node[4].asDouble()) COLOR_BULLISH else COLOR_BEARISH
                )
            }

            fun createRealtimeVolumeData(klineData: KlineData): VolumeData {
                return VolumeData(
                    klineData.time,
                    klineData.volume,
                    if (klineData.open >= klineData.close) COLOR_BULLISH else COLOR_BEARISH
                )
            }
        }
    }
}
