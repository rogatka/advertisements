package com.advertisement.auth.service

import com.advertisement.auth.configuration.SecurityProperties
import com.advertisement.auth.model.TokenType
import com.advertisement.auth.model.User
import com.advertisement.auth.utils.RsaUtils
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Clock
import java.time.Instant
import java.util.*


@Service
class TokenServiceImpl(
    val securityProperties: SecurityProperties,
    val clock: Clock
) : TokenService {

    val TOKEN_SUBJECT: String = "user"
    val TOKEN_CLAIM_USER: String = "userId"

    override fun generateToken(user: User, tokenType: TokenType): Mono<String> {
        val now = Instant.now(clock)
        val lifetimeSeconds = when (tokenType) {
            TokenType.ACCESS -> securityProperties.jwt.accessToken.lifetimeSeconds
            TokenType.REFRESH -> securityProperties.jwt.refreshToken.lifetimeSeconds
        }
        return Mono.just(
            JWT.create()
                .withClaim(TOKEN_CLAIM_USER, user.id.toString())
                .withSubject(TOKEN_SUBJECT)
                .withExpiresAt(Date(now.plusSeconds(lifetimeSeconds).toEpochMilli()))
                .withIssuedAt(Date(now.toEpochMilli()))
                .withNotBefore(Date(now.toEpochMilli()))
                .sign(getTokenAlgorithm(tokenType))
        )
    }

    override fun verifyToken(token: String, tokenType: TokenType): Mono<DecodedJWT> {
        return Mono.just(JWT.require(getTokenAlgorithm(tokenType)).build())
            .map { verifier -> verifier.verify(token) }
    }

    fun getTokenAlgorithm(tokenType: TokenType): Algorithm {
        val rsaPublicKey = when (tokenType) {
            TokenType.ACCESS -> securityProperties.jwt.accessToken.rsaPublicKeyBase64
            TokenType.REFRESH -> securityProperties.jwt.refreshToken.rsaPublicKeyBase64
        }
        val rsaPrivateKey = when (tokenType) {
            TokenType.ACCESS -> securityProperties.jwt.accessToken.rsaPrivateKeyBase64
            TokenType.REFRESH -> securityProperties.jwt.refreshToken.rsaPrivateKeyBase64
        }
        return Algorithm.RSA512(
            RsaUtils.getPublicKey(rsaPublicKey) as RSAPublicKey,
            RsaUtils.getPrivateKey(rsaPrivateKey) as RSAPrivateKey
        )
    }
}