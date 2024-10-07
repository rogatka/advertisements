package com.advertisement.payment.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "pay-service")
data class PayServiceProperties @ConstructorBinding constructor(
    val baseUrl: String,
    val payPath: String,
    val username: String,
    val password: String,
    val retry: Retry
)

data class Retry (
    val maxAttempts: Int,
    val minBackoffSeconds: Int
)