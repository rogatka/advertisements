package com.advertisement.ads.service

import com.advertisement.ads.model.Advertisement
import com.advertisement.ads.repository.AdvertisementRepository
import com.advertisement.ads.service.messaging.producer.AdvertisementMessageProducer
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AdvertisementServiceImpl(
    val advertisementRepository: AdvertisementRepository,
    val advertisementMessageProducer: AdvertisementMessageProducer
) : AdvertisementService {

    override fun search(q: String, pageable: Pageable): Flux<Advertisement> {
        return advertisementRepository.search(q, pageable)
    }

    override fun findAllByAuthorId(authorId: String): Flux<Advertisement> {
        return advertisementRepository.findAllByAuthorId(authorId)
    }

    override fun findAllPublishedPageable(pageable: Pageable): Flux<Advertisement> {
        return advertisementRepository.findAllPublishedPageable(pageable)
    }

    override fun countPublished(searchQuery: String?): Mono<Long> {
        return advertisementRepository.countPublished(searchQuery)
    }


    override fun findById(id: String): Mono<Advertisement> {
        return advertisementRepository.findById(id)
    }

    override fun incrementViewsCounter(id: String): Mono<Unit> {
        return Mono.just(advertisementMessageProducer.incrementViewsMessage(id))
    }
}