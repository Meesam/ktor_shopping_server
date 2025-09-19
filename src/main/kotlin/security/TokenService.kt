package com.meesam.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.security.SecureRandom
import java.time.Instant
import java.util.Base64
import java.util.Date
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

data class AccessTokenResult(
    val token: String,
    val expiresAt: Instant
)

data class RefreshTokenPlain(
    val token: String,   // opaque string we return to the client
    val userId: Long,
    val jti: String,
    val expiresAt: Instant
)

class TokenService(
    issuer: String,
    audience: String,
    secret: String,
    private val accessTtl: Duration = 15.minutes,
    private val refreshTtl: Duration = 14.days,
) {
    private val algorithm = Algorithm.HMAC256(secret)
    private val issuerClaim = issuer
    private val audienceClaim = audience
    private val secureRandom = SecureRandom()

    fun createAccessToken(userId: Long, role: String?): AccessTokenResult {
        val now = Instant.now()
        val exp = now.plusSeconds(accessTtl.inWholeSeconds)
        val token = JWT.create()
            .withIssuer(issuerClaim)
            .withAudience(audienceClaim)
            .withSubject(userId.toString())
            .withClaim("role", role)
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(exp))
            .withJWTId(UUID.randomUUID().toString())
            .sign(algorithm)
        return AccessTokenResult(token, exp)
    }

    fun createRefreshToken(userId: Long): RefreshTokenPlain {
        val bytes = ByteArray(64)
        secureRandom.nextBytes(bytes)
        val opaque = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
        val now = Instant.now()
        val exp = now.plusSeconds(refreshTtl.inWholeSeconds)
        return RefreshTokenPlain(
            token = opaque,
            userId = userId,
            jti = UUID.randomUUID().toString(),
            expiresAt = exp
        )
    }
}