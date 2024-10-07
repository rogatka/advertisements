package com.advertisement.ads.model

import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "ads")
data class Advertisement(
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
    val contactInfo: ContactInfo,
    val creationDate: Instant,
    val publicationDate: Instant?,
    val priorityInfo: PriorityInfo,
    val history: List<AdvertisementHistory> = listOf(),
    val views: Long = 0
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Advertisement

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
        if (contactInfo != other.contactInfo) return false
        if (creationDate != other.creationDate) return false
        if (publicationDate != other.publicationDate) return false
        if (priorityInfo != other.priorityInfo) return false
        if (history != other.history) return false
        if (views != other.views) return false

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
        result = 31 * result + contactInfo.hashCode()
        result = 31 * result + creationDate.hashCode()
        result = 31 * result + (publicationDate?.hashCode() ?: 0)
        result = 31 * result + priorityInfo.hashCode()
        result = 31 * result + history.hashCode()
        result = 31 * result + views.hashCode()
        return result
    }

    override fun toString(): String {
        return "Advertisement(id=$id, title='$title', status=$status, description='$description', expirationDate=$expirationDate, city=$city, priceRu=$priceRu, imagesInfo=$imagesInfo, authorId='$authorId', contactInfo=$contactInfo, creationDate=$creationDate, publicationDate=$publicationDate, priorityInfo=$priorityInfo, history=$history, views=$views)"
    }

}