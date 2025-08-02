package com.zunza.buythedip_kotlin.chat.scheduler

import com.zunza.buythedip_kotlin.chat.service.ChatMessageStreamConsumer
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ChatMessageScheduler(
    private val chatMessageStreamConsumer: ChatMessageStreamConsumer
) {

    @Scheduled(fixedDelay = 10_000)
    @SchedulerLock(
        name = "ChatMessageScheduler_processBatch()",
        lockAtMostFor = "8s",
        lockAtLeastFor = "2s"
    )
    fun processBatch() {
        chatMessageStreamConsumer.consumeAndStore()
    }
}

