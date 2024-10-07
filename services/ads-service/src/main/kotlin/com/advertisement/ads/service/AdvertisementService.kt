package com.advertisement.ads.service

import com.advertisement.ads.model.Advertisement
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface AdvertisementService {
    fun search(q: String, pageable: Pageable): Flux<Advertisement>
    fun findAllByAuthorId(authorId: String): Flux<Advertisement>
    fun findAllPublishedPageable(pageable: Pageable): Flux<Advertisement>
    fun countPublished(searchQuery: String?): Mono<Long>
    fun findById(id: String): Mono<Advertisement>
    fun incrementViewsCounter(id: String): Mono<Unit>
}