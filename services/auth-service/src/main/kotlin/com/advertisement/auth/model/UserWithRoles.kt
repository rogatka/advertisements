package com.advertisement.auth.model

import java.time.Instant
import java.util.*

data class UserWithRoles(
    val id: UUID,
    val phone: String,
    val firstName: String?,
    val lastName: String?,
    val createdAt: Instant,
    val roles: List<String>
)