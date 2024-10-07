package com.advertisement.auth.model

import java.time.Instant
import java.util.*

data class User(
    val id: UUID,
    val phone: String,
    val firstName: String?,
    val lastName: String?,
    val createdAt: Instant
)