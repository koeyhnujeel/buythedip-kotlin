package com.zunza.buythedip_kotlin.crypto.scheduler

import com.zunza.buythedip_kotlin.crypto.service.BinanceService
import com.zunza.buythedip_kotlin.crypto.service.CryptoMarketDataService
import com.zunza.buythedip_kotlin.crypto.service.CryptoService
import io.github.oshai.kotlinlogging.KotlinLogging
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {  }

@Service
class CryptoMarketDataScheduler(
    private val binanceService: BinanceService,
    private val cryptoService: CryptoService,
    private val cryptoMarketDataService: CryptoMarketDataService,
) {
    companion object{
        private const val SYMBOL_SUFFIX = "USDT"
        private const val OPEN_PRICE_INTERVAL = "1d"
        private const val OPEN_PRICE_LIMIT = 1

        private const val AGGREGATION_WINDOW_MINUTE = 30
        private const val TOP_N = 50L
    }

    @Scheduled(cron = "7 0 0 * * *", zone = "UTC")
    @SchedulerLock(name = "CryptoMarketScheduler_updateDailyOpenPrice")
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
                cryptoMarketDataService.updateOpenPrice(crypto.symbol + SYMBOL_SUFFIX, openPrice)
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
        cryptoMarketDataService.aggregateTickerVolumesInRange(AGGREGATION_WINDOW_MINUTE)
        val tickers = cryptoMarketDataService.getTopNTickersByVolume(TOP_N)

        if (tickers.isNullOrEmpty()) {
            return
        }

        cryptoMarketDataService.cacheTopNTickerSymbols(tickers)
        cryptoMarketDataService.publishTopNTickerSummaries(tickers)
    }
}
