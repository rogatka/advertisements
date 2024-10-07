package com.advertisement.auth.model

import java.time.Instant

data class Role(
    val id: Long,
    val name: String,
    val createdAt: Instant
)