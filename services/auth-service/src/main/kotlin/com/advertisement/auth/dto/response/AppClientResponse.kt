package com.advertisement.auth.dto.response

import java.time.Instant
import java.util.*

data class AppClientResponse(
    val id: UUID,
    val name: String,
    val password: String?,
    val disabled: Boolean,
    val createdAt: Instant,
    val disabledAt: Instant?

) {
    override fun toString(): String {
        return "AppClient(id=$id, name='$name', disabled=$disabled, createdAt=$createdAt, disabledAt=$disabledAt)"
    }
}