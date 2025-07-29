package com.zunza.buythedip_kotlin.user.exception

import com.zunza.buythedip_kotlin.common.CustomException
import jakarta.servlet.http.HttpServletResponse

class UserNotFoundException : CustomException {
    constructor(accountId: String?) : super("존재하지 않는 사용자입니다. ACCOUNT ID: $accountId")
    constructor(userId: Long) : super("존재하지 않는 사용자입니다. USER ID: $userId")

    override fun getStatusCode(): Int = HttpServletResponse.SC_NOT_FOUND
}
