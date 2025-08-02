package com.zunza.buythedip_kotlin.security.websocket.exception

import com.zunza.buythedip_kotlin.common.CustomException
import jakarta.servlet.http.HttpServletResponse.*

class StompAuthenticationFailedException (
    val command: String
) : CustomException(MESSAGE + command) {
    companion object {
        private const val MESSAGE = "Authentication principal not set in STOMP request [COMMAND]: "
    }

    override fun getStatusCode() = SC_UNAUTHORIZED
}
