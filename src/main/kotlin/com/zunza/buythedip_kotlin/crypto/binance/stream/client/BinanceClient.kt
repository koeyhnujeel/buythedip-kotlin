package com.zunza.buythedip_kotlin.crypto.binance.stream.client

import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono


@Service
class BinanceClient(
    webClientBuilder: WebClient.Builder,
    rateLimiterRegistry: RateLimiterRegistry
) {
    private val webClient: WebClient = webClientBuilder
        .baseUrl("https://data-api.binance.vision")
        .build()

    private val binanceKlineApiLimiter = rateLimiterRegistry.rateLimiter("binance-kline-api")

    fun getKlineData(symbol: String, interval: String, limit: Int): Mono<String> {
            return webClient.get()
                .uri { uriBuilder ->
                    uriBuilder.path("/api/v3/klines")
                        .queryParam("symbol", symbol)
                        .queryParam("interval", interval)
                        .queryParam("limit", limit)
                        .build()
                }
                .retrieve()
                .bodyToMono(String::class.java)
                .transform(RateLimiterOperator.of(binanceKlineApiLimiter))
    }
}
