package com.advertisement.payment.dto.request

data class ReversePaymentRequest (
    val userId: String,
    val businessId: String,
    val amount: Long
)