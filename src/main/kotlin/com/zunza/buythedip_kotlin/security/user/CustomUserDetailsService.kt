package com.zunza.buythedip_kotlin.security.user

import com.zunza.buythedip_kotlin.user.exception.UserNotFoundException
import com.zunza.buythedip_kotlin.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails? {
        val user = userRepository.findByAccountId(username)
            ?: throw UserNotFoundException(username)

        return CustomUserDetails(user)
    }
}
