package com.zunza.buythedip_kotlin.user.controller

import com.zunza.buythedip_kotlin.user.dto.LoginRequest
import com.zunza.buythedip_kotlin.user.dto.LoginResponse
import com.zunza.buythedip_kotlin.user.dto.SignupRequest
import com.zunza.buythedip_kotlin.user.dto.ValidationRequest
import com.zunza.buythedip_kotlin.user.service.AuthService
import jakarta.servlet.http.HttpServletResponse.*
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@RestController
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/api/auth/validation/check-availability")
    fun checkAvailability(
        @RequestBody validationRequest: ValidationRequest
    ): ResponseEntity<Unit> {
        authService.checkAvailability(validationRequest)
        return ResponseEntity.status(SC_OK).build()
    }

    @PostMapping("/api/auth/signup")
    fun signup(
        @Valid @RequestBody signupRequest: SignupRequest
    ): ResponseEntity<Unit> {
        authService.signup(signupRequest)
        return ResponseEntity.status(SC_CREATED).build()
    }

    @PostMapping("/api/auth/login")
    fun login(
        @RequestBody loginRequest: LoginRequest
    ): ResponseEntity<LoginResponse> {
        val loginSuccessDto = authService.login(loginRequest)
        val responseCookie = getHttpOnlyCookie(loginSuccessDto.refreshToken, 7L)

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
            .body(LoginResponse(loginSuccessDto.nickname, loginSuccessDto.accessToken))
    }

    @PostMapping("/api/auth/logout")
    fun logout(
        @AuthenticationPrincipal userId: Long
    ): ResponseEntity<Unit> {
        authService.logout(userId)
        val responseCookie = getHttpOnlyCookie("", 0)

        return ResponseEntity
            .status(SC_OK)
            .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
            .build()
    }

    fun getHttpOnlyCookie(
        value: String,
        maxAge: Long
    ): ResponseCookie = ResponseCookie.from("refreshToken", value)
            .httpOnly(true)
            .path("/")
            .maxAge(Duration.ofDays(maxAge))
            .build()

}
