package com.zunza.buythedip_kotlin.crypto.controller

import com.zunza.buythedip_kotlin.crypto.dto.KlineResponse
import com.zunza.buythedip_kotlin.crypto.service.BinanceService
import com.zunza.buythedip_kotlin.crypto.service.CryptoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class CryptoController(
    private val binanceService: BinanceService,
    private val cryptoService: CryptoService
) {
    @GetMapping("/api/cryptos/price/top-volume")
    fun getTopNTickerSummaries() = ResponseEntity.ok(cryptoService.getTopNTickerSummaries())


    @GetMapping("/api/cryptos/kline")
    fun getHistoricalKlineData(
        @RequestParam(name = "symbol") symbol: String,
        @RequestParam(name = "interval") interval: String = "15m",
    ): ResponseEntity<List<KlineResponse>> {
        return ResponseEntity.ok(binanceService.getHistoricalKlineData(symbol, interval).block()!!)
    }

    @GetMapping("/api/cryptos/{id}/info")
    fun getCryptoInformation(
        @PathVariable id: Long
    ) = ResponseEntity.ok(cryptoService.getCryptoInformation(id))
}

