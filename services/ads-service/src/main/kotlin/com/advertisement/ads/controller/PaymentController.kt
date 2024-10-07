package com.advertisement.ads.controller

import com.advertisement.ads.dto.UserInfo
import com.advertisement.ads.dto.request.CreateAdvertisementPaymentRequest
import com.advertisement.ads.exception.ApiBadRequestException
import com.advertisement.ads.exception.ApiNotFoundException
import com.advertisement.ads.model.AdvertisementPayment
import com.advertisement.ads.model.PriorityType
import com.advertisement.ads.repository.AdvertisementRepository
import com.advertisement.ads.service.AdvertisementPaymentService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/payments")
class PaymentController(
    val advertisementPaymentService: AdvertisementPaymentService,
    val advertisementRepository: AdvertisementRepository
) {
    @PostMapping
    fun create(@RequestBody createAdvertisementPaymentRequest: CreateAdvertisementPaymentRequest, authentication: Authentication)
            : Mono<ResponseEntity<AdvertisementPayment>> {
        val userId = (authentication.principal as UserInfo).id.toString()
        return advertisementRepository.findById(createAdvertisementPaymentRequest.advertisementId)
            .switchIfEmpty(Mono.error(ApiNotFoundException("Advertisement with id = ${createAdvertisementPaymentRequest.advertisementId} not found")))
            .flatMap { adv ->
                val currentPriority = adv.priorityInfo.priority
                if (currentPriority == PriorityType.NORMAL) {
                    advertisementPaymentService.pay(
                        userId,
                        createAdvertisementPaymentRequest.advertisementId,
                        createAdvertisementPaymentRequest.amount
                    )
                } else {
                    Mono.error(ApiBadRequestException("Advertisement already has the highest priority"))
                }
            }
            .map { r -> ResponseEntity.ok(r) }
    }
}