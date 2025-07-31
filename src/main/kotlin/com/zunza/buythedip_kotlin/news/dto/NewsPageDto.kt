package com.zunza.buythedip_kotlin.news.dto


data class NewsCursorRequest(
    val newsId: Long = 0,
    val datetime: Long = 0
)

data class NewsPreview(
    val newsId: Long = 0,
    val headline: String = "",
    val datetime: Long = 0
)

data class NextCursor(
    val newsId: Long? = null,
    val datetime: Long? = null
)

data class NewsPageResponse(
    val newsPreviews: List<NewsPreview> = emptyList(),
    val nextCursor: NextCursor? = null,
    val hasMore: Boolean = false
)
