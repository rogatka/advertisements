package com.advertisement.auth.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "sms-service")
data class SmsServiceProperties @ConstructorBinding constructor(
    val baseUrl: String,
    val sendSmsPath: String,
    val getTokenPath: String,
    val username: String,
    val password: String,
    val retry: Retry
)

data class Retry (
    val maxAttempts: Int,
    val minBackoffSeconds: Int
)