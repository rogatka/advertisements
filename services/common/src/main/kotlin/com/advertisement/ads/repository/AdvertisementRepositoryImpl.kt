package com.advertisement.ads.repository

import com.advertisement.ads.model.Advertisement
import com.advertisement.ads.model.AdvertisementStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class AdvertisementRepositoryImpl(
    val mongoTemplate: ReactiveMongoTemplate
): AdvertisementRepository {

    override fun search(q: String, pageable: Pageable): Flux<Advertisement> {
        val query = Query()
        query.addCriteria(Criteria()
            .andOperator(Criteria.where("status").`is`(AdvertisementStatus.PUBLISHED.name),
                Criteria().orOperator(
                        Criteria.where("title").regex(q, "i"),
                        Criteria.where("description").regex(q, "i"),
                )))
            .with(Sort.by(Sort.Direction.ASC, "priorityInfo.priority")) // HIGH -> NORMAL
            .with(pageable)
        return mongoTemplate.find(query, Advertisement::class.java)
    }

    override fun findAllByAuthorId(authorId: String): Flux<Advertisement> {
        val query = Query()
        query.addCriteria(Criteria.where("authorId").`is`(authorId))
        return mongoTemplate.find(query, Advertisement::class.java)
    }

    override fun findAllPublishedPageable(pageable: Pageable): Flux<Advertisement> {
        val query = Query()
        query
            .addCriteria(Criteria.where("status").`is`(AdvertisementStatus.PUBLISHED.name))
            .with(pageable)
            .with(Sort.by(Sort.Direction.ASC, "priorityInfo.priority")) // HIGH -> NORMAL
            .with(pageable.sort)
        return mongoTemplate.find(query, Advertisement::class.java)
    }

    override fun countPublished(searchQuery: String?): Mono<Long> {
        val query = Query()
        query.addCriteria(Criteria.where("status").`is`(AdvertisementStatus.PUBLISHED.name))
        searchQuery?.let { query.addCriteria(Criteria()
            .orOperator(
                Criteria.where("title").regex(searchQuery, "i"),
                Criteria.where("description").regex(searchQuery, "i"),
            ))
        }
        return mongoTemplate.count(query, Advertisement::class.java)
    }

    override fun findById(id: String): Mono<Advertisement> {
        return mongoTemplate.findById(id, Advertisement::class.java)
    }

    override fun save(advertisement: Advertisement): Mono<Advertisement> {
        return mongoTemplate.save(advertisement)
    }
}