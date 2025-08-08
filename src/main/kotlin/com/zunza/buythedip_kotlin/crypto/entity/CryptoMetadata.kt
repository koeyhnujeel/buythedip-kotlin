package com.zunza.buythedip_kotlin.crypto.entity

import com.zunza.buythedip_kotlin.crypto.converter.StringListConverter
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
class CryptoMetadata(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val logo: String,

    @Lob
    @Column(columnDefinition = "TEXT")
    val description: String,

    @Column(columnDefinition = "TEXT")
    @Convert(converter = StringListConverter::class)
    val website: List<String>,

    @Column(columnDefinition = "TEXT")
    @Convert(converter = StringListConverter::class)
    val twitter: List<String>,

    @Column(columnDefinition = "TEXT")
    @Convert(converter = StringListConverter::class)
    val explorer: List<String>,

    @Column(columnDefinition = "TEXT")
    @Convert(converter = StringListConverter::class)
    val tagNames: List<String>,

    @CreationTimestamp
    val createdAt: LocalDateTime,

    @UpdateTimestamp
    val updatedAt: LocalDateTime,

    @OneToOne
    @JoinColumn(name = "crypto_id")
    val crypto: Crypto
)
