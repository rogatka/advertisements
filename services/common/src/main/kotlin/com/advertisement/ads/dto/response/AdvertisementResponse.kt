package com.advertisement.ads.dto.response

import com.advertisement.ads.model.*
import java.time.Instant

data class AdvertisementResponse (
    var id: String?,
    val title: String,
    val status: AdvertisementStatus,
    val description: String,
    val expirationDate: Instant,
    val city: City,
    val priceRu: PriceRu,
    val previewImage: ByteArray?,
    val imagesInfo: List<ImageInfo> = listOf(),
    val authorId: String,
    val creationDate: Instant,
    val publicationDate: Instant?,
    val priorityType: PriorityType
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AdvertisementResponse

        if (id != other.id) return false
        if (title != other.title) return false
        if (status != other.status) return false
        if (description != other.description) return false
        if (expirationDate != other.expirationDate) return false
        if (city != other.city) return false
        if (priceRu != other.priceRu) return false
        if (previewImage != null) {
            if (other.previewImage == null) return false
            if (!previewImage.contentEquals(other.previewImage)) return false
        } else if (other.previewImage != null) return false
        if (imagesInfo != other.imagesInfo) return false
        if (authorId != other.authorId) return false
        if (creationDate != other.creationDate) return false
        if (publicationDate != other.publicationDate) return false
        if (priorityType != other.priorityType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + title.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + expirationDate.hashCode()
        result = 31 * result + city.hashCode()
        result = 31 * result + priceRu.hashCode()
        result = 31 * result + (previewImage?.contentHashCode() ?: 0)
        result = 31 * result + imagesInfo.hashCode()
        result = 31 * result + authorId.hashCode()
        result = 31 * result + creationDate.hashCode()
        result = 31 * result + (publicationDate?.hashCode() ?: 0)
        result = 31 * result + priorityType.hashCode()
        return result
    }

    override fun toString(): String {
        return "AdvertisementResponse(id=$id, title='$title', status=$status, description='$description', expirationDate=$expirationDate, city=$city, priceRu=$priceRu, imagesInfo=$imagesInfo, authorId='$authorId', creationDate=$creationDate, publicationDate=$publicationDate, priorityType=$priorityType)"
    }


}