package com.advertisement.payment.dto.request

data class CreatePaymentRequest (
    val userId: String,
    val businessId: String,
    val amount: Long
)