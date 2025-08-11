package com.zunza.buythedip_kotlin.crypto.scheduler

import com.zunza.buythedip_kotlin.crypto.binance.stream.manager.KlineStreamManager
import com.zunza.buythedip_kotlin.crypto.service.BinanceService
import com.zunza.buythedip_kotlin.crypto.service.CryptoService
import io.github.oshai.kotlinlogging.KotlinLogging
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.data.redis.core.KeyScanOptions
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {  }

@Service
class CryptoMarketDataScheduler(
    private val binanceService: BinanceService,
    private val cryptoService: CryptoService,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val klineStreamManager: KlineStreamManager
) {
    companion object{
        private const val SYMBOL_SUFFIX = "USDT"
        private const val OPEN_PRICE_INTERVAL = "1d"
        private const val OPEN_PRICE_LIMIT = 1

        private const val AGGREGATION_WINDOW_MINUTE = 30
        private const val TOP_N = 50L
    }

    @Scheduled(cron = "3 0 0 * * *", zone = "UTC")
    @SchedulerLock(name = "CryptoMarketScheduler_updateDailyOpenPrice")
//    @EventListener(ApplicationReadyEvent::class)
fun updateDailyOpenPrice() {
        try {
            val cryptos = cryptoService.getAllCrypto()

            cryptos.forEach { crypto ->
                val openPrice = binanceService.getOpenPrice(
                    crypto.symbol + SYMBOL_SUFFIX,
                    OPEN_PRICE_INTERVAL,
                    OPEN_PRICE_LIMIT
                ).block()!!

                logger.info { "symbol: ${crypto.symbol + SYMBOL_SUFFIX} / open price: $openPrice" }
                cryptoService.updateOpenPrice(crypto.symbol + SYMBOL_SUFFIX, openPrice)
            }
        }  catch (e: Exception) {
            logger.warn { e.message }
        }
    }

    // TODO: 초기 데이터
    @Scheduled(cron = "0/10 * * * * *")
    @SchedulerLock(
        name = "CryptoMarketDataScheduler_updateTickerVolume"
    )
    fun updateTickerVolume() {
        cryptoService.aggregateTickerVolumesInRange(AGGREGATION_WINDOW_MINUTE)
        val tickers = cryptoService.getTopNTickersByVolume(TOP_N)

        if (tickers.isNullOrEmpty()) {
            return
        }

        cryptoService.cacheTopNTickerSymbols(tickers)
        cryptoService.publishTopNTickerSummaries(tickers)
    }

    @Scheduled(cron = "0 0/30 * * * *")
    @SchedulerLock(
        name = "CryptoMarketDataScheduler_unsubscribeKline"
    )
    fun unsubscribeKline() {
        val keyPrefix = "/topic/crypto/kline/"
        val keys = getSubscriptionKlineKeys("$keyPrefix*")

        val params = keys.mapNotNull { key ->
            if (redisTemplate.opsForValue().get(key) == 0) {
                redisTemplate.delete(key)
                val (symbol, interval) = key.removePrefix(keyPrefix).split("/", limit = 2)
                "${symbol.lowercase()}usdt@kline_$interval"
            } else {
                null
            }
        }

        klineStreamManager.unsubscribeCombinedKline(params)
    }

    private fun getSubscriptionKlineKeys(pattern: String): Set<String> {
        val scanOptions = KeyScanOptions.scanOptions()
            .match(pattern)
            .count(50)
            .build()

        return redisTemplate.execute { connection ->
            val stringSerializer = redisTemplate.stringSerializer
            val results = mutableSetOf<String>()

            connection.keyCommands().scan(scanOptions).use { cursor ->
                while (cursor.hasNext()) {
                    val key = stringSerializer.deserialize(cursor.next())
                    key?.let { results.add(it) }
                }
            }
            results
        } ?: emptySet()
    }
}
