package com.zunza.buythedip_kotlin.news.dto

data class TranslatedNewsDto(
    val headline: String?,
    val summary: String?,
    val source: String,
    val url: String,
    val datetime: Long
) {
    companion object {
        fun createOf(parsed: Map<String, String>, fetchedNews: FetchedNewsDto): TranslatedNewsDto =
            TranslatedNewsDto(
                parsed["headline"],
                parsed["summary"],
                fetchedNews.source,
                fetchedNews.url,
                fetchedNews.datetime
            )
    }
}

