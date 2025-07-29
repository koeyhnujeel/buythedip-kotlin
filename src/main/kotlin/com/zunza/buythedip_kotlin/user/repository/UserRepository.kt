package com.zunza.buythedip_kotlin.user.repository

import com.zunza.buythedip_kotlin.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun existsByAccountId(accountId: String): Boolean
    fun existsByNickname(nickname: String): Boolean
    fun findByAccountId(accountId: String?): User?
}
