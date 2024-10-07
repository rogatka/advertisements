package com.advertisement.auth.dto.request

import com.advertisement.auth.dto.OtpType
import jakarta.validation.constraints.Pattern

data class OtpRequest(
    @get:Pattern(regexp = "\\d{5,20}")
    val phone: String,
    val otpType: OtpType
)
