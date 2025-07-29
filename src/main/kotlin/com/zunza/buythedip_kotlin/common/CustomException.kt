package com.zunza.buythedip_kotlin.common

abstract class CustomException(message: String) : RuntimeException(message) {
    abstract fun getStatusCode(): Int;
}
