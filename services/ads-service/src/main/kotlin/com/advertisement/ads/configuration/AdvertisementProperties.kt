package com.advertisement.ads.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "advertisement")
data class AdvertisementProperties @ConstructorBinding constructor(
    val durationDays: Int,
    val priorityDays: Int,
)