package com.zunza.buythedip_kotlin.news.dto

import io.finnhub.api.models.MarketNews

data class FetchedNewsDto(
    val headline: String,
    val summary: String,
    val source: String,
    val url: String,
    val datetime: Long
) {
    companion object {
        fun createFrom(marketNews: MarketNews): FetchedNewsDto {
            return FetchedNewsDto(
                marketNews.headline ?: "",
                marketNews.summary ?: "",
                marketNews.source ?: "",
                marketNews.url ?: "",
                marketNews.datetime ?: 0
            )
        }
    }
}
