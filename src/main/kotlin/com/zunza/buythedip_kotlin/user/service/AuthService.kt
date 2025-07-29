package com.zunza.buythedip_kotlin.user.service

import com.zunza.buythedip_kotlin.user.dto.SignupRequest
import com.zunza.buythedip_kotlin.user.dto.ValidationRequest
import com.zunza.buythedip_kotlin.user.dto.ValidationRequest.ValidationType.*
import com.zunza.buythedip_kotlin.user.entity.User
import com.zunza.buythedip_kotlin.user.exception.DuplicateAccountIdException
import com.zunza.buythedip_kotlin.user.exception.DuplicateNicknameException
import com.zunza.buythedip_kotlin.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
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
