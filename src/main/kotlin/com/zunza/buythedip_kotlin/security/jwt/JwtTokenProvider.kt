package com.zunza.buythedip_kotlin.security.jwt

import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

private val logger = KotlinLogging.logger {  }

@Component
class JwtTokenProvider(
    @Value("\${jwt.key}")
    private val key: String,

    @Value("\${jwt.access-token-expire-time}")
    private val accessTokenExpireTime: Long,

    @Value("\${jwt.refresh-token-expire-time}")
    private val refreshTokenExpireTime: Long
) {
    fun generateAccessToken(userId: Long, roles: Collection<GrantedAuthority?>?): String {
        val authorities = roles?.map { it?.authority }
        val key = getKey()
        val now = Instant.now()

        return Jwts.builder()
            .subject(userId.toString())
            .claim("auth", authorities)
            .issuedAt(Date(now.toEpochMilli()))
            .expiration(Date(now.plusMillis(accessTokenExpireTime).toEpochMilli()))
            .signWith(key, Jwts.SIG.HS256)
            .compact()
    }

    fun generateRefreshToken(userId: Long): String {
        val key = getKey()
        val now = Instant.now()

        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(Date(now.toEpochMilli()))
            .expiration(Date(now.plusMillis(refreshTokenExpireTime).toEpochMilli()))
            .signWith(key, Jwts.SIG.HS256)
            .compact()
    }

    fun validateToken(token: String?): Boolean {
        try {
            Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token)
            return true
        } catch (e: JwtException) {
            logger.warn { "${e.message}" }
            throw e
        }
    }

    fun getAuthentication(token: String): Authentication {
        val claims = getClaims(token)
        val userId = claims.subject as String
        val authorities = claims["auth"] as? List<*>

        return UsernamePasswordAuthenticationToken(userId.toLong(),
            null,
            authorities?.filterIsInstance<String>()
                ?.map { SimpleGrantedAuthority(it) })
    }

    fun getClaims(token: String): Claims {
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).payload
    }


    private fun getKey(): SecretKey {
        return Keys.hmacShaKeyFor(this.key.toByteArray())
    }
}
