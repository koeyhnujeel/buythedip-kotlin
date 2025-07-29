package com.zunza.buythedip_kotlin.security.jwt


const val AUTHORIZATION_HEADER: String = "Authorization"
const val TOKEN_PREFIX: String = "Bearer "

fun extractJwtToken(authorizationHeader: String?): String? {
    return authorizationHeader
        ?.takeIf { it.startsWith(TOKEN_PREFIX) }
        ?.substring(TOKEN_PREFIX.length )
}
