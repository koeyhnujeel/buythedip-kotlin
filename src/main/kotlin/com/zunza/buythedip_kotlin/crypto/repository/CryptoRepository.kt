package com.zunza.buythedip_kotlin.crypto.repository

import com.zunza.buythedip_kotlin.crypto.dto.CryptoInformationResponse
import com.zunza.buythedip_kotlin.crypto.dto.CryptoWithLogoDto
import com.zunza.buythedip_kotlin.crypto.entity.Crypto
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CryptoRepository : JpaRepository<Crypto, Long> {

    @Query("""
        SELECT new com.zunza.buythedip_kotlin.crypto.dto.CryptoWithLogoDto(
        c.id,
        c.name,
        c.symbol,
        m.logo
        )
        FROM Crypto c
        JOIN c.metadata m
        """)
    @Cacheable(cacheNames = ["CRYPTO:WITH:LOGO"])
    fun findAllWithLogo(): List<CryptoWithLogoDto>

    @Query("""
        SELECT new com.zunza.buythedip_kotlin.crypto.dto.CryptoInformationResponse(
        c.name,
        c.symbol,
        m.logo,
        m.description,
		m.website,
		m.twitter,
		m.explorer,
		m.tagNames
        )
        FROM Crypto c
        JOIN c.metadata m
        WHERE c.id = :id
    """)
    @Cacheable(cacheNames = ["CRYPTO:INFORMATION"])
    fun findByIdWithMetadata(@Param("id") id :Long): CryptoInformationResponse
}
