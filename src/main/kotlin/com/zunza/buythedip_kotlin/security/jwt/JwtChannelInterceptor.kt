package com.zunza.buythedip_kotlin.security.jwt

import com.zunza.buythedip_kotlin.security.websocket.exception.InvalidAuthorizationHeaderException
import com.zunza.buythedip_kotlin.security.user.CustomUserDetails
import com.zunza.buythedip_kotlin.security.websocket.exception.StompAuthenticationFailedException
import com.zunza.buythedip_kotlin.user.exception.UserNotFoundException
import com.zunza.buythedip_kotlin.user.repository.UserRepository
import io.jsonwebtoken.Claims
import org.springframework.data.repository.findByIdOrNull
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import kotlin.text.startsWith


@Component
class JwtChannelInterceptor(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository
) : ChannelInterceptor {

    override fun preSend(
        message: Message<*>,
        channel: MessageChannel
    ): Message<*>? {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
            ?: return message

        return when (accessor.command) {
            StompCommand.CONNECT -> handleConnect(accessor, message)
            StompCommand.SEND, StompCommand.SUBSCRIBE -> handleAuthenticated(accessor, message)
            StompCommand.DISCONNECT -> message
            else -> message
        }
    }

    private fun handleConnect(
        accessor: StompHeaderAccessor,
        message: Message<*>
    ): Message<*> {
        val endpoint = accessor.sessionAttributes?.get("endpoint") as String
        if (!endpoint.startsWith("/ws/chat")) return message

        val authorization = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER)
        if (authorization.isNullOrBlank() || !authorization.startsWith(TOKEN_PREFIX)) {
            throw InvalidAuthorizationHeaderException()
        }

        val token = authorization.removePrefix(TOKEN_PREFIX)
        jwtTokenProvider.validateToken(token)

        val claims = jwtTokenProvider.getClaims(token)
        accessor.user = getAuthentication(claims)

        return message
    }

    private fun handleAuthenticated(
        accessor: StompHeaderAccessor,
        message: Message<*>
    ): Message<*> {
        val endpoint = accessor.sessionAttributes?.get("endpoint") as String
        if (!endpoint.startsWith("/ws/chat")) return message

        return when {
            accessor.user == null -> throw StompAuthenticationFailedException(accessor.command.toString())
            else -> message
        }
    }

    private fun getAuthentication(claims: Claims): Authentication {
        val userId = claims.subject as String
        val user = userRepository.findByIdOrNull(userId.toLong())
            ?: throw UserNotFoundException(userId.toLong())

        val details = CustomUserDetails(user)
        return UsernamePasswordAuthenticationToken(
            details,
            null,
            details.authorities
        )
    }
}
