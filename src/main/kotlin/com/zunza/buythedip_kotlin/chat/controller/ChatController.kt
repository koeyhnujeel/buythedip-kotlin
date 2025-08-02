package com.zunza.buythedip_kotlin.chat.controller

import com.zunza.buythedip_kotlin.chat.dto.ChatMessageDto
import com.zunza.buythedip_kotlin.chat.service.ChatService
import com.zunza.buythedip_kotlin.security.user.CustomUserDetails
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class ChatController(
    private val chatService: ChatService
) {
    @MessageMapping("/chat/room/public")
    fun sendMessage(
        chatMessage: ChatMessageDto,
        principal: Principal
    ) {
        val authentication = principal as Authentication
        val details = authentication.principal as CustomUserDetails
        chatService.sendMessage(details.getUserId(), chatMessage)
    }
}
