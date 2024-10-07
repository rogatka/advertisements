package com.advertisement.auth.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "security")
data class SecurityProperties @ConstructorBinding constructor(
    val internalAuth: InternalAuth,
    val otp: Otp,
    val jwt: Jwt
)

data class InternalAuth(
    val registrationKey: String,
    val hashingSalt: String
)

data class Otp(
    val hashingSalt: String,
    val lifetimeSeconds: Long
)

data class Jwt(
    val accessToken: AccessToken,
    val refreshToken: RefreshToken
)

data class AccessToken(
    val rsaPublicKeyBase64: String,
    val rsaPrivateKeyBase64: String,
    val lifetimeSeconds: Long
)

data class RefreshToken(
    val rsaPublicKeyBase64: String,
    val rsaPrivateKeyBase64: String,
    val lifetimeSeconds: Long,
    val hashingSalt: String
)