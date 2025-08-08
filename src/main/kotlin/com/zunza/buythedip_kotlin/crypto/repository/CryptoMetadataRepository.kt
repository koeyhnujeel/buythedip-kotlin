package com.zunza.buythedip_kotlin.crypto.repository

import com.zunza.buythedip_kotlin.crypto.entity.CryptoMetadata
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CryptoMetadataRepository : JpaRepository<CryptoMetadata, Long> {
}
