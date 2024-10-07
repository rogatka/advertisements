package com.advertisement.ads.controller

import com.advertisement.ads.dto.UserInfo
import com.advertisement.ads.dto.response.AdvertisementResponse
import com.advertisement.ads.dto.response.AdvertisementWithContactInfoResponse
import com.advertisement.ads.service.AdvertisementService
import com.advertisement.ads.utils.toAdvertisementResponse
import com.advertisement.ads.utils.toAdvertisementWithContactInfoResponse
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/advertisements")
class AdvertisementController(
    val advertisementService: AdvertisementService,
) {

    @GetMapping
    fun find(@RequestParam("q", required = false) q: String?, pageable: Pageable): ResponseEntity<Mono<Page<AdvertisementResponse>>> {
        return ResponseEntity.ok(
                (q?.let { advertisementService.search(q, pageable) } ?: advertisementService.findAllPublishedPageable(pageable))
                .map { a -> a.toAdvertisementResponse() }
                .collectList()
                .zipWith(advertisementService.countPublished(q))
                .map { a -> PageImpl(a.t1, pageable, a.t2) })
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: String): Mono<ResponseEntity<AdvertisementWithContactInfoResponse>> {
        return advertisementService.findById(id)
            .map { a -> a.toAdvertisementWithContactInfoResponse() }
            .map { r -> ResponseEntity.ok(r) }
    }

    @GetMapping("/my")
    fun getMyAdvertisements(authentication: Authentication): ResponseEntity<Flux<AdvertisementResponse>> {
        val user = authentication.principal as UserInfo
        return ResponseEntity.ok(
            advertisementService.findAllByAuthorId(user.id.toString())
            .map { a -> a.toAdvertisementResponse() }
        )
    }

    @PostMapping("/{id}:viewed")
    fun incrementViewsCounter(@PathVariable("id") id: String)
    : ResponseEntity<Mono<Unit>> {
        return ResponseEntity.ok(advertisementService.incrementViewsCounter(id))
    }
}