package com.advertisement.ads.dto.request

import com.advertisement.ads.model.ImageInfo
import com.advertisement.ads.model.PriceRu

data class UpdateAdvertisementRequest (
    val title: String?,
    val description: String?,
    val cityId: String?,
    val priceRu: PriceRu?,
    val images: List<ImageInfo>?,
    val contactPhone: String?,
    val contactFirstName: String?,
    val contactLastName: String?
)