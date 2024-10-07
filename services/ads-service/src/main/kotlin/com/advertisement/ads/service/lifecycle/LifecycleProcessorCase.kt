package com.advertisement.ads.service.lifecycle

import com.advertisement.ads.model.AdvertisementAction
import com.advertisement.ads.model.AdvertisementStatus

data class LifecycleProcessorCase (
    val currentStatus: AdvertisementStatus,
    val action: AdvertisementAction
)