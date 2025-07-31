package com.zunza.buythedip_kotlin.news.exception

import com.zunza.buythedip_kotlin.common.CustomException
import jakarta.servlet.http.HttpServletResponse.*

class NewsNotFoundException(
    val newsId: Long
) : CustomException(MESSAGE + newsId) {

    companion object {
        private const val MESSAGE = "존재하지 않는 뉴스 입니다. NEWS ID: "
    }

    override fun getStatusCode(): Int  = SC_NOT_FOUND
}
