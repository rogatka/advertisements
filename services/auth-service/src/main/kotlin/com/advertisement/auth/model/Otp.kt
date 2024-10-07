package com.advertisement.auth.model

import com.advertisement.auth.dto.OtpType
import java.time.Instant
import java.util.*

data class Otp(
    val id: UUID,
    val phone: String,
    val hashedCode: ByteArray,
    val otpType: OtpType,
    val createdAt: Instant
    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Otp

        if (id != other.id) return false
        if (phone != other.phone) return false
        if (!hashedCode.contentEquals(other.hashedCode)) return false
        if (otpType != other.otpType) return false
        if (createdAt != other.createdAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + phone.hashCode()
        result = 31 * result + hashedCode.contentHashCode()
        result = 31 * result + otpType.hashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }

    override fun toString(): String {
        return "Otp(id=$id, phone='$phone', otpType=$otpType, createdAt=$createdAt)"
    }

}