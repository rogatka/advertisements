package com.advertisement.auth.model

import java.time.Instant
import java.util.*

data class UserRole(
    val id: UUID,
    val userId: UUID,
    val roleId: Long,
    val createdAt: Instant
)