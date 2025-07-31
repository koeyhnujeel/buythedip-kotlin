package com.zunza.buythedip_kotlin.news.service

import com.zunza.buythedip_kotlin.news.dto.TranslatedNewsDto
import com.zunza.buythedip_kotlin.news.entity.News
import com.zunza.buythedip_kotlin.news.repository.NewsRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {  }

@Service
class NewsStorageService(
    private val newsRepository: NewsRepository
) {
    @CacheEvict(cacheNames = ["NEWS:PAGE"], allEntries = true)
    suspend fun saveNews(translatedNewsList: List<TranslatedNewsDto>) {
        withContext(Dispatchers.IO) {
            newsRepository.saveAll(translatedNewsList.map { translatedNews ->
                convertToNewsEntity(translatedNews)
            })
            logger.info { "News successfully saved." }
        }
    }

    private fun convertToNewsEntity(translatedNews: TranslatedNewsDto): News {
        return News.createFrom(translatedNews)
    }
}
