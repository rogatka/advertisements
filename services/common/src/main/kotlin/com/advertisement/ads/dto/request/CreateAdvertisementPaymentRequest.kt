package com.advertisement.ads.dto.request

data class CreateAdvertisementPaymentRequest (
    val advertisementId: String,
    val amount: Long
)