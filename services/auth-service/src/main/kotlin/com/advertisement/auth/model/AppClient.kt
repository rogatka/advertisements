package com.advertisement.auth.model

import java.time.Instant
import java.util.*

data class AppClient(
    val id: UUID,
    val name: String,
    val password: ByteArray,
    val disabled: Boolean,
    val createdAt: Instant,
    val disabledAt: Instant?

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppClient

        if (id != other.id) return false
        if (name != other.name) return false
        if (!password.contentEquals(other.password)) return false
        if (disabled != other.disabled) return false
        if (createdAt != other.createdAt) return false
        if (disabledAt != other.disabledAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + password.contentHashCode()
        result = 31 * result + disabled.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + (disabledAt?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "AppClient(id=$id, name='$name', disabled=$disabled, createdAt=$createdAt, disabledAt=$disabledAt)"
    }
}