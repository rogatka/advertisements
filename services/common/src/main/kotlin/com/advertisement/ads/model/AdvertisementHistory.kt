package com.advertisement.ads.model

import java.time.Instant

data class AdvertisementHistory (
    val advertisementStatus: AdvertisementStatus,
    val setAt: Instant,
    val setBy: StatusChangeInitiator
) {
    data class StatusChangeInitiator(
        val id: String?,
        val type: String
    )
}