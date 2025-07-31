package com.zunza.buythedip_kotlin.news.repository

import com.zunza.buythedip_kotlin.news.dto.NewsCursorRequest
import com.zunza.buythedip_kotlin.news.dto.NewsPageResponse

interface CustomNewsRepository {
    fun findNewsPageByCursor(newsCursor: NewsCursorRequest): NewsPageResponse
}
