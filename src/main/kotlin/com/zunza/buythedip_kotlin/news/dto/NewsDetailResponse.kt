package com.zunza.buythedip_kotlin.news.dto

import com.zunza.buythedip_kotlin.news.entity.News

data class NewsDetailResponse(
    val newsId: Long = 0,
    val headline: String = "",
    val summary: String = "",
    val source: String = "",
    val url: String = "",
    val datetime: Long = 0
) {
    companion object {
        fun createFrom(news: News): NewsDetailResponse {
             return NewsDetailResponse(
                news.id,
                news.headline,
                news.summary,
                news.source,
                news.url,
                news.datetime
            )
        }
    }
}
