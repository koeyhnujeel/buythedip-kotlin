package com.zunza.buythedip_kotlin.news.service

import com.zunza.buythedip_kotlin.news.dto.NewsCursorRequest
import com.zunza.buythedip_kotlin.news.dto.NewsDetailResponse
import com.zunza.buythedip_kotlin.news.dto.NewsPageResponse
import com.zunza.buythedip_kotlin.news.exception.NewsNotFoundException
import com.zunza.buythedip_kotlin.news.repository.NewsRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class NewsService(
    private val newsRepository: NewsRepository
) {
    @Cacheable(cacheNames = ["NEWS:PAGE"], key = "#newsCursor.datetime")
    fun getNewsPage(newsCursor: NewsCursorRequest): NewsPageResponse {
        return newsRepository.findNewsPageByCursor(newsCursor)
    }

    @Cacheable(cacheNames = ["NEWS:DETAIL"], key = "#newsId")
    fun getNewsDetail(newsId: Long): NewsDetailResponse {
        return newsRepository.findByIdOrNull(newsId)
            ?.let { news ->  NewsDetailResponse.createFrom(news) }
            ?: throw NewsNotFoundException(newsId)
    }
}
