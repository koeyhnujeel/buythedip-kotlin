package com.zunza.buythedip_kotlin.user.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
class User private constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val accountId: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    val nickname: String,

    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.MIN,

    @UpdateTimestamp
    val updatedAt: LocalDateTime = LocalDateTime.MIN
) {
    companion object {
        fun createUser(
            accountId: String,
            password: String,
            nickname: String
        ): User {
            return User(
                accountId = accountId,
                password = password,
                nickname = nickname
            )
        }
    }
}
