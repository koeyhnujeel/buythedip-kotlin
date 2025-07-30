package com.zunza.buythedip_kotlin.news.entity

import com.zunza.buythedip_kotlin.news.dto.TranslatedNewsDto
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class News(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val headline: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val summary: String,

    @Column(nullable = false)
    val source: String,

    @Column(nullable = false)
    val url: String,

    @Column(nullable = false)
    val datetime: Long
) {
    companion object {
        fun createFrom(dto: TranslatedNewsDto): News {
            return News(
                headline = dto.headline!!,
                summary = dto.summary!!,
                source = dto.source,
                url =  dto.url,
                datetime =  dto.datetime
            )
        }
    }
}
