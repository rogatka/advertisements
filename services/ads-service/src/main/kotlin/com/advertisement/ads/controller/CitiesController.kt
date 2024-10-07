package com.advertisement.ads.controller

import com.advertisement.ads.model.City
import com.advertisement.ads.service.CityService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/api/v1/cities")
class CitiesController(
    val cityService: CityService
) {

    @GetMapping
    fun findCities(@RequestParam("name", required = false) name: String?)
    : ResponseEntity<Flux<City>> {
        return ResponseEntity.ok(
            if (name == null) cityService.findAll() else cityService.findByCityLikeOrderByCityAsc(name)
        )
    }
}