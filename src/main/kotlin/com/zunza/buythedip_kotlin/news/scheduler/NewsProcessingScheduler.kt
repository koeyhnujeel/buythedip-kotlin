package com.zunza.buythedip_kotlin.news.scheduler

import com.zunza.buythedip_kotlin.news.service.NewsFetchService
import com.zunza.buythedip_kotlin.news.service.NewsStorageService
import com.zunza.buythedip_kotlin.news.service.NewsTranslationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class NewsProcessingScheduler(
    private val newsFetchService: NewsFetchService,
    private val newsTranslationService: NewsTranslationService,
    private val newsStorageService: NewsStorageService,
    private val applicationScope: CoroutineScope
) {
    @SchedulerLock(
        name = "NewsProcessingScheduler_processNewsPipeline",
        lockAtLeastFor = "3s",
    )
    @Scheduled(cron = "0/10 * * * * *")
    fun processNewsPipeline() {
        applicationScope.launch {
            val fetchedNewsList = newsFetchService.fetchNews()

            if (fetchedNewsList.isNotEmpty()) {
                val translatedNewsList = newsTranslationService.translateNews(fetchedNewsList)
                newsStorageService.saveNews(translatedNewsList)
            }
        }
    }
}

