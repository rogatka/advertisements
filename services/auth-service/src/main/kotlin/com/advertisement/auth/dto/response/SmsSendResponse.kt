package com.advertisement.auth.dto.response

data class SmsSendResponse(
    val send: Boolean,
    val code: String?,
    val message: String
)