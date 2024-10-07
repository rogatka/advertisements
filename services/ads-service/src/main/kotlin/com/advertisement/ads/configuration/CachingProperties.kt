package com.advertisement.ads.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "caching")
data class CachingProperties @ConstructorBinding constructor(
    val defaultTtlMinutes: Int,
    val advertisements: CacheItem,
    val cities: CacheItem,
) {
    data class CacheItem (
        val cacheName: String,
        val ttlMinutes: Int
    )
}
