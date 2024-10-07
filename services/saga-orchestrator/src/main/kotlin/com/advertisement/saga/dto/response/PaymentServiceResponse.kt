package com.advertisement.saga.dto.response

import java.time.Instant
import java.util.*

data class PaymentServiceResponse (
    val id: UUID,
    val businessId: String,
    val transactionId: String,
    val createdAt: Instant
)