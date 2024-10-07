package com.advertisement.auth.dto.request

import jakarta.validation.constraints.NotNull

data class InternalRegistrationRequest(
    @field:NotNull
    val appName: String
)