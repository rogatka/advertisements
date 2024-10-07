package com.advertisement.ads.model

import java.time.Instant

data class PaymentStatusHistory (
    val status: PaymentStatus,
    val setAt: Instant
)