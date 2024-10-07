package com.advertisement.ads.repository

import com.advertisement.ads.model.City
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface CityRepository : ReactiveMongoRepository<City, String> {

    fun findByCityLikeOrderByCityAsc(city: String): Flux<City>
}
