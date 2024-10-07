package com.advertisement.auth.dto.request

import jakarta.validation.constraints.NotNull

data class UserVerificationRequest(
    @field:NotNull
    val token: String
)