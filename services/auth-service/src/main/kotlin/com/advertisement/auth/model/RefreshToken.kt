package com.advertisement.auth.model

import java.time.Instant
import java.util.*

data class RefreshToken(
    val id: UUID,
    val userId: UUID,
    val hash: ByteArray,
    val revoked: Boolean,
    val createdAt: Instant
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RefreshToken

        if (id != other.id) return false
        if (userId != other.userId) return false
        if (!hash.contentEquals(other.hash)) return false
        if (revoked != other.revoked) return false
        if (createdAt != other.createdAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + hash.contentHashCode()
        result = 31 * result + revoked.hashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }

    override fun toString(): String {
        return "RefreshToken(id=$id, userId=$userId, revoked=$revoked, createdAt=$createdAt)"
    }
}