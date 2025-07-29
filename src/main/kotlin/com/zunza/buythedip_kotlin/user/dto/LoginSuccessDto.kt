package com.zunza.buythedip_kotlin.user.dto

data class LoginSuccessDto(
    val accessToken: String,
    val refreshToken: String,
    val nickname: String
)
