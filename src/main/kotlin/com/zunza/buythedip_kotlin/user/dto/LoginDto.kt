package com.zunza.buythedip_kotlin.user.dto


data class LoginRequest(
    val accountId: String,
    val password: String
)

data class LoginResponse(
    val nickname: String,
    val accessToken: String
)
