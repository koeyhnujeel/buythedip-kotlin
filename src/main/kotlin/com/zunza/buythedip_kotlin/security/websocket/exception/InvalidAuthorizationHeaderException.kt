package com.zunza.buythedip_kotlin.security.websocket.exception

import com.zunza.buythedip_kotlin.common.CustomException
import jakarta.servlet.http.HttpServletResponse.*

class InvalidAuthorizationHeaderException : CustomException(MESSAGE) {

    companion object {
        private const val MESSAGE = "Invalid Authorization header"
    }

    override fun getStatusCode() = SC_UNAUTHORIZED
}
