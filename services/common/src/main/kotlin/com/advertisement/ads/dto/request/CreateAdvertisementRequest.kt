package com.advertisement.ads.dto.request

import com.advertisement.ads.model.ContactInfo
import com.advertisement.ads.model.ImageInfo
import com.advertisement.ads.model.PriceRu

data class CreateAdvertisementRequest (
    val title: String,
    val description: String,
    val cityId: String,
    val priceRu: PriceRu,
    val images: List<ImageInfo> = listOf(),
    val contactInfo: ContactInfo,
)