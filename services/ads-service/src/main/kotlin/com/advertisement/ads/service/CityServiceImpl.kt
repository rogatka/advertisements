package com.advertisement.ads.service

import com.advertisement.ads.configuration.CachingProperties
import com.advertisement.ads.model.City
import com.advertisement.ads.repository.CityRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.time.Duration

@Service
class CityServiceImpl(
    val cityRepository: CityRepository,
    val cachingProperties: CachingProperties
) : CityService {

    @Cacheable(value = ["cities_cache"])
    override fun findAll(): Flux<City> {
        return cityRepository.findAll()
            //TODO remove hardcoded value
            .cache(Duration.ofMinutes(cachingProperties.cities.ttlMinutes.toLong() + 10))
    }

    override fun findByCityLikeOrderByCityAsc(city: String): Flux<City> {
        return cityRepository.findByCityLikeOrderByCityAsc(city)
    }
}