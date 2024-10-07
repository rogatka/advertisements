package com.advertisement.ads.model

import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "advertisement_payments")
data class AdvertisementPayment(
    var id: String?,
    val userId: String,
    val advertisementId: String,
    val amount: PaymentAmount,
    val status: PaymentStatus,
    val creationDate: Instant,
    val updateDate: Instant? = null,
    val details: String?,
    val statusHistory: List<PaymentStatusHistory> = listOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AdvertisementPayment

        if (id != other.id) return false
        if (userId != other.userId) return false
        if (advertisementId != other.advertisementId) return false
        if (amount != other.amount) return false
        if (status != other.status) return false
        if (creationDate != other.creationDate) return false
        if (details != other.details) return false
        if (statusHistory != other.statusHistory) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + userId.hashCode()
        result = 31 * result + advertisementId.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + creationDate.hashCode()
        result = 31 * result + details.hashCode()
        result = 31 * result + statusHistory.hashCode()
        return result
    }

    override fun toString(): String {
        return "AdvertisementPayment(id=$id, userId='$userId', advertisementId='$advertisementId', amount=$amount, status=$status, creationDate=$creationDate, details='$details', statusHistory=$statusHistory)"
    }
}