package com.zunza.buythedip_kotlin.news.repository

import com.zunza.buythedip_kotlin.news.entity.News
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NewsRepository : JpaRepository<News, Long>, CustomNewsRepository {
}
