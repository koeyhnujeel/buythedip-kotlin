package com.zunza.buythedip_kotlin.security.websocket

import com.zunza.buythedip_kotlin.security.websocket.exception.InvalidAuthorizationHeaderException
import com.zunza.buythedip_kotlin.security.websocket.exception.StompAuthenticationFailedException
import io.jsonwebtoken.JwtException
import jakarta.servlet.http.HttpServletResponse.*
import org.springframework.messaging.Message
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.MessageBuilder
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler

class StompExceptionHandler : StompSubProtocolErrorHandler() {

    override fun handleClientMessageProcessingError(
        clientMessage: Message<ByteArray?>?,
        ex: Throwable
    ): Message<ByteArray?>? {
        val actualException = ex.cause
        when (actualException) {
            is JwtException -> return createErrorMessage(actualException.message!!)
            is InvalidAuthorizationHeaderException -> return createErrorMessage(actualException.message!!)
            is StompAuthenticationFailedException -> return createErrorMessage(actualException.message!!)
        }

        return null
    }

    private fun createErrorMessage(message: String): Message<ByteArray?> {
        val headers = StompHeaderAccessor.create(StompCommand.ERROR)
        headers.message = message
        headers.addNativeHeader("error-code", SC_UNAUTHORIZED.toString())

        return MessageBuilder.createMessage(
            message.toByteArray(),
            headers.messageHeaders
        )
    }
}
