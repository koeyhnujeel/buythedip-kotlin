package com.zunza.buythedip_kotlin.news.service

import com.zunza.buythedip_kotlin.news.dto.FetchedNewsDto
import com.zunza.buythedip_kotlin.news.entity.Topic
import com.zunza.buythedip_kotlin.news.entity.Topic.*
import com.zunza.buythedip_kotlin.news.repository.TopicRepository
import io.finnhub.api.apis.DefaultApi
import io.finnhub.api.models.MarketNews
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {  }

@Service
class NewsFetchService(
    private val topicRepository: TopicRepository,
    private val finnhubClient: DefaultApi
) {
    suspend fun fetchNews(): List<FetchedNewsDto> {
        return withContext(Dispatchers.IO) {

            val topic = findOrCreateTopic()
            val marketNewsList: List<MarketNews> = finnhubClient.marketNews(Category.CRYPTO.value, topic.minId)

            if (marketNewsList.isEmpty()) {
                return@withContext emptyList()
            }

            logger.info { "News successfully fetched." }

            updateTopicMinId(topic, marketNewsList)
            convertToFetchedNewsDto(marketNewsList)
        }
    }

    private fun findOrCreateTopic(): Topic {
        return topicRepository.findByIdOrNull(Category.CRYPTO)
            ?: topicRepository.save(Topic(Category.CRYPTO))
    }

    private fun convertToFetchedNewsDto(marketNewsList: List<MarketNews>): List<FetchedNewsDto> {
        return marketNewsList.map { marketNews -> FetchedNewsDto.createFrom(marketNews) }
    }

    private fun updateTopicMinId(topic: Topic, newsList: List<MarketNews>) {
        val maxId = newsList.maxOfOrNull { it.id ?: Long.MIN_VALUE }

        maxId?.let { id ->
            if (id > topic.minId) {
                topic.minId = id
                topicRepository.save(topic)
            }
        }
    }
}
