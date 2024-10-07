package com.advertisement.saga.dto.request

data class CreatePaymentRequest (
    val userId: String,
    val advertisementId: String,
    val amount: Long
)