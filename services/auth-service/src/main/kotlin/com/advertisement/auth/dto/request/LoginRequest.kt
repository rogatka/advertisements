package com.advertisement.auth.dto.request

import jakarta.validation.constraints.Pattern

data class LoginRequest(
    @get:Pattern(regexp = "\\d{5,20}")
    val phone: String,
    val code: String
)