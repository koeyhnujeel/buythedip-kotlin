package com.zunza.buythedip_kotlin.security.user

import com.zunza.buythedip_kotlin.user.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    private val user: User
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority?>? {
        return mutableListOf(GrantedAuthority { "ROLE_USER" })
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun getUsername(): String {
        return user.accountId
    }

    fun getUserId(): Long {
        return user.id
    }

    fun getNickname(): String {
        return user.nickname
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}
