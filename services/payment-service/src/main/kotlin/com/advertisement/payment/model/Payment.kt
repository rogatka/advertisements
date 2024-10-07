package com.advertisement.payment.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("payments", schema = "payment")
data class Payment(
    @Id
    val id: UUID,
    val businessId: String,
    val transactionId: String,
    val accountFrom: String,
    val accountTo: String,
    val amount: Long,
    val createdAt: Instant
)