package com.advertisement.ads.repository

import com.advertisement.ads.model.AdvertisementPayment
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface AdvertisementPaymentRepository : ReactiveMongoRepository<AdvertisementPayment, String> {
    fun findByAdvertisementId(advertisementId: String): Mono<AdvertisementPayment>
}