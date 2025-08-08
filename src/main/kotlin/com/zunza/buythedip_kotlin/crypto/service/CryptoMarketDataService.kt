package com.zunza.buythedip_kotlin.crypto.service

import com.zunza.buythedip_kotlin.crypto.repository.CryptoMarketDataRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {  }

@Service
class CryptoMarketDataService(
    private val cryptoMarketDataRepository: CryptoMarketDataRepository,
) {
    companion object {
        private const val ZONE_ID = "UTC"
        private const val BUCKET_TTL_MINUTE = 31L
    }

    fun updateOpenPrice(symbol: String, openPrice: Double) {
        cryptoMarketDataRepository.saveOpenPrice(symbol, openPrice)
    }
}
