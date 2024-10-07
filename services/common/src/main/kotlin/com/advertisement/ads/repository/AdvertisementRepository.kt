package com.advertisement.ads.repository

import com.advertisement.ads.model.Advertisement
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface AdvertisementRepository {

    fun search(q: String, pageable: Pageable): Flux<Advertisement>

    fun findAllByAuthorId(authorId: String): Flux<Advertisement>

    fun findAllPublishedPageable(pageable: Pageable): Flux<Advertisement>

    fun countPublished(searchQuery: String?): Mono<Long>

    fun findById(id: String): Mono<Advertisement>

    fun save(advertisement: Advertisement): Mono<Advertisement>
}
