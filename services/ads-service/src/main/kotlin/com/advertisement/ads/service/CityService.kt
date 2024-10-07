package com.advertisement.ads.service

import com.advertisement.ads.model.City
import reactor.core.publisher.Flux

interface CityService {

    fun findAll(): Flux<City>
    fun findByCityLikeOrderByCityAsc(city: String): Flux<City>
}