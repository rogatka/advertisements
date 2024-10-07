package com.advertisement.ads.model

import java.time.Instant

data class PriorityInfo (
    val priority: PriorityType = PriorityType.NORMAL,
    val paidDate: Instant? = null,
    val expirationDate: Instant? = null,
    val history: List<PriorityHistory> = mutableListOf()
) {
    data class PriorityHistory(
        val priority: PriorityType,
        val paidDate: Instant? = null,
        val expirationDate: Instant? = null,
    )
}