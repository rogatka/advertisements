package com.advertisement.ads.utils

import com.advertisement.ads.dto.response.AdvertisementResponse
import com.advertisement.ads.dto.response.AdvertisementWithContactInfoResponse
import com.advertisement.ads.model.Advertisement

fun Advertisement.toAdvertisementWithContactInfoResponse() = AdvertisementWithContactInfoResponse(
    id = id,
    title = title,
    status = status,
    description = description,
    expirationDate = expirationDate,
    city = city,
    priceRu = priceRu,
    previewImage = previewImage,
    imagesInfo = imagesInfo,
    authorId = authorId,
    contactInfo = contactInfo,
    creationDate = creationDate,
    publicationDate = publicationDate,
    priorityInfo = priorityInfo
)

fun Advertisement.toAdvertisementResponse() = AdvertisementResponse(
    id = id,
    title = title,
    status = status,
    description = description,
    expirationDate = expirationDate,
    city = city,
    priceRu = priceRu,
    previewImage = previewImage,
    imagesInfo = imagesInfo,
    authorId = authorId,
    creationDate = creationDate,
    publicationDate = publicationDate,
    priorityType = priorityInfo.priority
)