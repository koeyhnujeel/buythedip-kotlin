package com.zunza.buythedip_kotlin.news.entity

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id

@Entity
class Topic(

    @Id
    @Enumerated(EnumType.STRING)
    val category: Category,

    var minId: Long = 0
) {
    enum class Category(val value: String) {
        CRYPTO("crypto")
    }
}
