package com.zunza.buythedip_kotlin.user.service

import com.zunza.buythedip_kotlin.infrastructure.redis.RefreshTokenRepository
import com.zunza.buythedip_kotlin.security.jwt.JwtTokenProvider
import com.zunza.buythedip_kotlin.security.user.CustomUserDetails
import com.zunza.buythedip_kotlin.user.dto.LoginRequest
import com.zunza.buythedip_kotlin.user.dto.LoginSuccessDto
import com.zunza.buythedip_kotlin.user.dto.SignupRequest
import com.zunza.buythedip_kotlin.user.dto.ValidationRequest
import com.zunza.buythedip_kotlin.user.dto.ValidationRequest.ValidationType.*
import com.zunza.buythedip_kotlin.user.entity.User
import com.zunza.buythedip_kotlin.user.exception.DuplicateAccountIdException
import com.zunza.buythedip_kotlin.user.exception.DuplicateNicknameException
import com.zunza.buythedip_kotlin.user.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    fun checkAvailability(validationRequest: ValidationRequest) {
        when (validationRequest.validationType) {
            ACCOUNT_ID -> checkAccountIdAvailability(validationRequest.value)
            NICKNAME -> checkNicknameAvailability(validationRequest.value)
        }
    }

    fun signup(signupRequest: SignupRequest) {
        val encodedPassword: String = passwordEncoder.encode(signupRequest.password)
        val user = User.createUser(signupRequest.accountId, encodedPassword, signupRequest.nickname)
        userRepository.save(user)
    }

    fun login(loginRequest: LoginRequest): LoginSuccessDto {
        val authenticationToken = UsernamePasswordAuthenticationToken(loginRequest.accountId, loginRequest.password)
        val authenticate = authenticationManager.authenticate(authenticationToken)
        val principal = authenticate.principal as CustomUserDetails

        val accessToken = jwtTokenProvider.generateAccessToken(principal.getUserId(), principal.authorities)
        val refreshToken = jwtTokenProvider.generateRefreshToken(principal.getUserId())
        refreshTokenRepository.save(principal.getUserId(), refreshToken)

        return LoginSuccessDto(accessToken, refreshToken, principal.getNickname())
    }

    fun logout(userId: Long) {
        refreshTokenRepository.delete(userId)
    }

    private fun checkAccountIdAvailability(value: String) {
        if (userRepository.existsByAccountId(value)) {
            throw DuplicateAccountIdException()
        }
    }

    private fun checkNicknameAvailability(value: String) {
        if (userRepository.existsByNickname(value)) {
            throw DuplicateNicknameException()
        }
    }
}
