package com.advertisement.payment.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "security")
data class SecurityProperties @ConstructorBinding constructor(
    val internalAuth: InternalAuth,
)

data class InternalAuth(
    val username: String,
    val password: String
)