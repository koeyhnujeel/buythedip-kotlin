package com.zunza.buythedip_kotlin.user.controller

import com.zunza.buythedip_kotlin.user.dto.SignupRequest
import com.zunza.buythedip_kotlin.user.dto.ValidationRequest
import com.zunza.buythedip_kotlin.user.service.AuthService
import jakarta.servlet.http.HttpServletResponse.*
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

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
}
