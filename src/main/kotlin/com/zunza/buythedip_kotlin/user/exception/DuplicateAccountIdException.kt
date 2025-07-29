package com.zunza.buythedip_kotlin.user.exception

import com.zunza.buythedip_kotlin.common.CustomException
import jakarta.servlet.http.HttpServletResponse

class DuplicateAccountIdException : CustomException(MESSAGE) {

    companion object {
        const val MESSAGE: String = "이미 사용 중인 아이디 입니다."
    }

    override fun getStatusCode(): Int {
        return HttpServletResponse.SC_CONFLICT
    }
}
