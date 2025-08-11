package com.zunza.buythedip_kotlin.crypto.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.buythedip_kotlin.crypto.binance.stream.client.BinanceClient
import com.zunza.buythedip_kotlin.crypto.dto.KlineResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {  }

@Service
class BinanceService(
    private val binanceClient: BinanceClient,
    private val objectMapper: ObjectMapper
) {
    fun getOpenPrice(symbol: String, interval: String, limit: Int): Mono<Double> {
        return binanceClient.getKlineData(symbol, interval, limit)
            .map { klineData ->  parseOpenPrice(klineData, symbol) }
            .onErrorReturn(Double.NaN)
    }

    fun getHistoricalKlineData(symbol: String, interval: String, limit: Int = 200): Mono<List<KlineResponse>> {
        return binanceClient.getKlineData(symbol + "USDT", interval, limit)
            .map { jsonString -> parseKlineData(jsonString) }
            .onErrorReturn(emptyList())
    }

    private fun parseOpenPrice(klineData: String, symbol: String): Double {
        val jsonNode = objectMapper.readTree(klineData)
        if (jsonNode.isNull || jsonNode.isEmpty) {
            logger.warn { "No Kline data found for symbol: $symbol" }
            return Double.NaN
        }

        val klineData = jsonNode.get(0)
        return klineData.get(1).asText().toDouble()
    }

    private fun parseKlineData(jsonString: String): List<KlineResponse> {
        val rootNode = objectMapper.readTree(jsonString)
        if (rootNode.isNull || rootNode.isEmpty) {
            logger.warn { "No Kline data found" }
            return emptyList()
        }

        return rootNode.map { klineData ->
            KlineResponse.createHistoricalKlineResponse(klineData)
        }
    }
}
