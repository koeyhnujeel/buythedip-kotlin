package com.zunza.buythedip_kotlin.crypto.service

import com.zunza.buythedip_kotlin.crypto.repository.CryptoRepository
import org.springframework.stereotype.Service

@Service
class CryptoService(
    private val cryptoRepository: CryptoRepository
) {
    fun getAllCrypto() = cryptoRepository.findAll()
}
