package com.zunza.buythedip_kotlin.crypto.service

import com.zunza.buythedip_kotlin.crypto.dto.CryptoWithLogoDto
import com.zunza.buythedip_kotlin.crypto.dto.SingleTickerPriceResponse
import com.zunza.buythedip_kotlin.crypto.dto.TopVolumeTickerPriceResponse
import com.zunza.buythedip_kotlin.crypto.dto.TopVolumeTickerSummaryResponse
import com.zunza.buythedip_kotlin.crypto.dto.TradeData
import com.zunza.buythedip_kotlin.crypto.repository.CryptoMarketDataRepository
import com.zunza.buythedip_kotlin.crypto.repository.CryptoRepository
import com.zunza.buythedip_kotlin.infrastructure.redis.constants.RedisKey.*
import com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub.Channels.*
import com.zunza.buythedip_kotlin.infrastructure.redis.pub_sub.RedisMessagePublisher
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val logger = KotlinLogging.logger {  }

@Service
class CryptoMarketDataService(
    private val cryptoMarketDataRepository: CryptoMarketDataRepository,
    private val cryptoRepository: CryptoRepository,
    private val redisMessagePublisher: RedisMessagePublisher
) {
    companion object {
        private const val ZONE_ID = "UTC"
        private const val BUCKET_TTL_MINUTE = 31L
    }

    fun updateOpenPrice(symbol: String, openPrice: Double) {
        cryptoMarketDataRepository.saveOpenPrice(symbol, openPrice)
    }

    fun accumulateMinuteTickerVolume(tradeData: TradeData) {
        cryptoMarketDataRepository.saveVolumeByMinute(
            generateCurrentMinuteBucketKey(tradeData.tradeTime),
            tradeData.symbol,
            tradeData.price * tradeData.quantity,
            BUCKET_TTL_MINUTE
        )
    }

    fun aggregateTickerVolumesInRange(range: Int) {
        val keys = generateKeysForLastNMinutes(range)
        cryptoMarketDataRepository.aggregateVolume(keys, AGGREGATED_VOLUME_KEY.value)
    }

    fun getTopNTickersByVolume(n: Long): Set<ZSetOperations.TypedTuple<Any>>? {
        return cryptoMarketDataRepository.findTopNTickers(n)
    }

    fun cacheTopNTickerSymbols(tickers: Set<ZSetOperations.TypedTuple<Any>>) {
        val symbols = tickers.map { ticker -> ticker.value.toString() }.toSet()
        cryptoMarketDataRepository.updateTopVolumeSymbols(symbols)
    }


    fun publishTopNTickerSummaries(tickers: Set<ZSetOperations.TypedTuple<Any>>) {
        val cryptoMap: Map<String, CryptoWithLogoDto> = cryptoRepository.findAllWithLogo()
            .associateBy { cryptoWithLogoDto -> cryptoWithLogoDto.symbol }

        redisMessagePublisher.publishMessage(
            TOP_VOLUME_TICKER_SUMMARY_CHANNEL.topic,
            convertToTopVolumeTickerSummaryResponse(cryptoMap, tickers)
        )
    }

    fun publishTopNTickerPrice(tradeData: TradeData) {
        val symbols = cryptoMarketDataRepository.findTopVolumeSymbols()

        if (tradeData.symbol in symbols) {
            redisMessagePublisher.publishMessage(
                TOP_VOLUME_TICKER_PRICE_CHANNEL.topic,
                convertToTopVolumeTickerPriceResponse(tradeData.symbol, tradeData.price)
            )
        }
    }

    fun publishSingleTickerPrice(tradeData: TradeData) {
        val singleTickerPriceResponse = SingleTickerPriceResponse(
            extractOriginalSymbol(tradeData.symbol),
            tradeData.price,
            getChangePrice(tradeData.symbol, tradeData.price),
            getChangeRate(tradeData.symbol, tradeData.price)
            )

        redisMessagePublisher.publishMessage(
            SINGLE_TICKER_PRICE_CHANNEL.topic,
            singleTickerPriceResponse
        )
    }

    private fun generateCurrentMinuteBucketKey(tradeTime: Long): String {
        val tradeDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(tradeTime),
            ZoneId.of(ZONE_ID)
        )

        return MINUTE_BUCKET_KEY_PREFIX.value + tradeDateTime.format(
            DateTimeFormatter.ofPattern("yyyyMMddHHmm")
        )
    }

    private fun generateKeysForLastNMinutes(minutes: Int): List<String> {
        val now = Instant.now()
        return (0 until minutes).map { i ->
            val time = now.minus(i.toLong(), ChronoUnit.MINUTES).toEpochMilli()
            generateCurrentMinuteBucketKey(time)
        }
    }

    private fun convertToTopVolumeTickerSummaryResponse(
        cryptoMap: Map<String, CryptoWithLogoDto>,
        tickers: Set<ZSetOperations.TypedTuple<Any>>
    ): List<TopVolumeTickerSummaryResponse> {
        return tickers.map { ticker ->
            val symbol = extractOriginalSymbol(ticker.value.toString())
            val cryptoWithLogo = cryptoMap[symbol]

            TopVolumeTickerSummaryResponse(
                cryptoWithLogo!!.id,
                cryptoWithLogo.name,
                cryptoWithLogo.symbol,
                cryptoWithLogo.logo,
                ticker.score!!
            )
        }
    }

    private fun convertToTopVolumeTickerPriceResponse(
        symbol: String,
        currentPrice: Double
    ): TopVolumeTickerPriceResponse {
        val originalSymbol = extractOriginalSymbol(symbol)
        val changeRate = getChangeRate(symbol, currentPrice)

        return TopVolumeTickerPriceResponse(originalSymbol, currentPrice, changeRate)
    }

    private fun extractOriginalSymbol(symbol: String) = symbol.removeSuffix("USDT")

    private fun getOpenPrice(symbol: String) = cryptoMarketDataRepository.findOpenPriceBySymbol(symbol)

    private fun getChangeRate(symbol: String, currentPrice: Double): Double {
        val openPrice = getOpenPrice(symbol)

        return if (openPrice == 0.0) 0.0
        else ((currentPrice - openPrice) / openPrice) * 100;
    }

    private fun getChangePrice(symbol: String, currentPrice: Double) = currentPrice - getOpenPrice(symbol)

}
