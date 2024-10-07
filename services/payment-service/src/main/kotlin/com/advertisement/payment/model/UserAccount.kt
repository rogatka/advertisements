package com.advertisement.payment.model

import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("user_accounts", schema = "payment")
data class UserAccount(
    val id: UUID,
    val userId: String,
    val accountId: String
)