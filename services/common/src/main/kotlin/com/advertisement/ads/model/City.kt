package com.advertisement.ads.model

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "cities")
data class City (
    var id: String?,
    val country: String,
    val city: String
)