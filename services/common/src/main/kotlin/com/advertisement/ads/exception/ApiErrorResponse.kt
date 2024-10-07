package com.advertisement.ads.exception

import java.time.LocalDateTime

data class ApiErrorResponse(
    val description: String,
    val timestamp: LocalDateTime
)