package com.advertisement.ads.controller.statemachine

import com.advertisement.ads.configuration.AdvertisementProperties
import com.advertisement.ads.dto.UserInfo
import com.advertisement.ads.dto.request.CreateAdvertisementRequest
import com.advertisement.ads.dto.request.UpdateAdvertisementRequest
import com.advertisement.ads.dto.response.AdvertisementWithContactInfoResponse
import com.advertisement.ads.exception.ApiNotFoundException
import com.advertisement.ads.model.*
import com.advertisement.ads.repository.AdvertisementRepository
import com.advertisement.ads.repository.CityRepository
import com.advertisement.ads.repository.FileRepository
import com.advertisement.ads.service.FileService
import com.advertisement.ads.utils.FileUtils
import com.advertisement.ads.utils.toAdvertisementWithContactInfoResponse
import com.advertisement.statemachine.service.WorkFlowService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit

@RestController
@RequestMapping("/api/v1/advertisements/statemachine")
@Deprecated(
    replaceWith = ReplaceWith("com.advertisement.ads.controller.AdvertisementManagementController"),
    message = "Spring State Machine (asynchronous approach) does not really fit"
)
class StateMachineAdvertisementManagementController(
    val advertisementRepository: AdvertisementRepository,
    val cityRepository: CityRepository,
    val advertisementProperties: AdvertisementProperties,
    val fileRepository: FileRepository,
    val fileService: FileService,
    val clock: Clock,
    val workFlowService: WorkFlowService
) {

    @PostMapping
    fun create(@RequestBody createAdvertisementRequest: CreateAdvertisementRequest, authentication: Authentication)
            : Mono<ResponseEntity<AdvertisementWithContactInfoResponse>> {
        return cityRepository.findById(createAdvertisementRequest.cityId)
            .switchIfEmpty(Mono.error(ApiNotFoundException("City not found")))
            .zipWith(Mono.just(createAdvertisementRequest.images.minByOrNull { image -> image.order }!!)
                .flatMap { i -> fileRepository.findById(i.id) }
                .switchIfEmpty(Mono.error(ApiNotFoundException("File not found")))
                .flatMap { file -> fileService.getFile(file.id.toString(), FileType.IMAGE) }
                .map { image -> FileUtils.scaleImage(image) }
            )
            .flatMap { tuple ->
                val now = Instant.now(clock)
                advertisementRepository.save(
                    Advertisement(
                        null,
                        createAdvertisementRequest.title,
                        AdvertisementStatus.CREATED,
                        createAdvertisementRequest.description,
                        now.plus(advertisementProperties.durationDays.toLong(), ChronoUnit.DAYS),
                        tuple.t1,
                        createAdvertisementRequest.priceRu,
                        tuple.t2,
                        createAdvertisementRequest.images,
                        (authentication.principal as UserInfo).id.toString(),
                        createAdvertisementRequest.contactInfo,
                        now,
                        null,
                        PriorityInfo(PriorityType.NORMAL)
                    )
                )
                    .map { ad -> workFlowService.executeTransition(ad, AdvertisementAction.CREATE)
                        .map { r -> r.resultType }
                        .last()
                        .thenReturn(ad)
                    }
            }
            .flatMap { a -> a }
            .map { a -> a.toAdvertisementWithContactInfoResponse() }
            .map { r -> ResponseEntity.ok(r) }
    }

    @PatchMapping("/{id}")
    fun update(
        @PathVariable("id") id: String,
        @RequestBody updateAdvertisementRequest: UpdateAdvertisementRequest,
        authentication: Authentication
    )
            : Mono<ResponseEntity<AdvertisementWithContactInfoResponse>> {
        val currentUser = authentication.principal as UserInfo
        return advertisementRepository.findById(id)
            .switchIfEmpty(Mono.error(ApiNotFoundException("Advertisement not found")))
            .filter { adv -> adv.authorId == currentUser.id.toString() || "MODERATOR" in currentUser.roles }
            .switchIfEmpty(Mono.error(AccessDeniedException("User is not an author of this advertisement nor moderator")))
            .flatMap { adv ->
                val updatedAdv = Advertisement(
                    id,
                    updateAdvertisementRequest.title ?: adv.title,
                    adv.status,
                    updateAdvertisementRequest.description ?: adv.description,
                    adv.expirationDate,
                    updateAdvertisementRequest.cityId?.let { cityRepository.findById(it).block() } ?: adv.city,
                    updateAdvertisementRequest.priceRu ?: adv.priceRu,
                    adv.previewImage,
                    //TODO delete images from storage if images were detached by user
                    updateAdvertisementRequest.images ?: adv.imagesInfo,
                    adv.authorId,
                    contactInfo(updateAdvertisementRequest, adv),
                    adv.creationDate,
                    adv.publicationDate,
                    adv.priorityInfo
                )
                advertisementRepository.save(updatedAdv)
                    .map { ad -> workFlowService.executeTransition(ad, AdvertisementAction.EDITED)
                        .map { r -> r.resultType }
                        .last()
                        .thenReturn(ad)
                    }
            }
            .flatMap { a -> a }
            .map { a -> a.toAdvertisementWithContactInfoResponse().apply { this.status = AdvertisementStatus.AWAITING } }
            .map { r -> ResponseEntity.ok(r) }
    }

    // PUBLISH, APPROVE, REJECT, DEACTIVATE, ACTIVATE
    @PostMapping("/{id}/actions")
    fun changeState(@PathVariable("id") id: String,
                    @RequestParam("action") action: AdvertisementAction): ResponseEntity<Mono<ResponseEntity<AdvertisementWithContactInfoResponse>>> {
        return ResponseEntity.ok(advertisementRepository.findById(id)
            .switchIfEmpty(Mono.error(ApiNotFoundException("Advertisement not found")))
            // Spring State Machine (asynchronous approach) does not really fit
            // we cannot just throw an error, for example, if user is not an ad's author and trying to edit it
            .map { ad -> workFlowService.executeTransition(ad, AdvertisementAction.EDITED)
                .map { r -> r.resultType }
                .last()
                .thenReturn(ad)
            }
            .flatMap { a -> a }
            .map { a -> a.toAdvertisementWithContactInfoResponse().apply { this.status = AdvertisementStatus.AWAITING } }
            .map { r -> ResponseEntity.ok(r) }
        )
    }

    private fun contactInfo(
        updateAdvertisementRequest: UpdateAdvertisementRequest,
        adv: Advertisement
    ): ContactInfo {
        return if (updateAdvertisementRequest.contactFirstName != null || updateAdvertisementRequest.contactLastName != null || updateAdvertisementRequest.contactPhone != null) {
            ContactInfo(
                updateAdvertisementRequest.contactPhone ?: adv.contactInfo.phone,
                updateAdvertisementRequest.contactFirstName ?: adv.contactInfo.firstName,
                updateAdvertisementRequest.contactLastName ?: adv.contactInfo.lastName
            )
        } else adv.contactInfo
    }
}
