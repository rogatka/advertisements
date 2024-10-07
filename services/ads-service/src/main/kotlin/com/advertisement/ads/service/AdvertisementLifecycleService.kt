package com.advertisement.ads.service

import com.advertisement.ads.model.Advertisement
import com.advertisement.ads.model.AdvertisementAction
import reactor.core.publisher.Mono

interface AdvertisementLifecycleService {

    fun executeTransition(
        advertisement: Advertisement,
        event: AdvertisementAction
    ): Mono<Advertisement>
}