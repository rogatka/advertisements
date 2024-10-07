package com.advertisement.ads.service

import com.advertisement.ads.model.AdvertisementPayment
import reactor.core.publisher.Mono

interface AdvertisementPaymentService {
    fun pay(userId: String, advertisementId: String, amount: Long): Mono<AdvertisementPayment>
}