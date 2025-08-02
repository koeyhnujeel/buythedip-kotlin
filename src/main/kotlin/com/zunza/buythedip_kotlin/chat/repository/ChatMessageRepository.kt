package com.zunza.buythedip_kotlin.chat.repository

import com.zunza.buythedip_kotlin.chat.entity.ChatMessage
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatMessageRepository : MongoRepository<ChatMessage, String> {
}
