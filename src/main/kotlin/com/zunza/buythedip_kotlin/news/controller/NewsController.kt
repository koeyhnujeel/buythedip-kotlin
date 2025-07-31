package com.zunza.buythedip_kotlin.news.controller

import com.zunza.buythedip_kotlin.news.dto.NewsCursorRequest
import com.zunza.buythedip_kotlin.news.dto.NewsDetailResponse
import com.zunza.buythedip_kotlin.news.dto.NewsPageResponse
import com.zunza.buythedip_kotlin.news.service.NewsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class NewsController(
    private val newsService: NewsService
) {
    @GetMapping("/api/news")
    fun getNewsPage(
        @ModelAttribute newsCursor: NewsCursorRequest
    ): ResponseEntity<NewsPageResponse> {
        return ResponseEntity.ok(newsService.getNewsPage(newsCursor))
    }

    @GetMapping("/api/news/{newsId}")
    fun getNewsDetail(
        @PathVariable newsId: Long
    ): ResponseEntity<NewsDetailResponse> {
        return ResponseEntity.ok(newsService.getNewsDetail(newsId))
    }
}
