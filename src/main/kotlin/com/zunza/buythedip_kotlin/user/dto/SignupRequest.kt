package com.zunza.buythedip_kotlin.user.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SignupRequest(

    @field:NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Size(min = 6, max = 20, message = "아이디는 6자 이상 20자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "아이디는 영문 소문자와 숫자만 사용 가능합니다.")
    val accountId: String,

    @field:NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, max = 24, message = "비밀번호는 8자 이상 24자 이하로 입력해주세요.")
    @Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "비밀번호는 8자 이상이며, 영문자, 숫자, 특수문자를 최소 1개 이상 포함해야 합니다."
    )
    val password: String,

    @field:NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$", message = "닉네임은 한글, 영문자, 숫자만 사용 가능합니다.")
    val nickname: String
)
