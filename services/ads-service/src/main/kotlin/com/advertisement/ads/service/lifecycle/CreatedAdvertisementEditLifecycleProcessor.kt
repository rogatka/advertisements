package com.advertisement.ads.service.lifecycle

import com.advertisement.ads.exception.ApiForbiddenException
import com.advertisement.ads.model.Advertisement
import com.advertisement.ads.model.AdvertisementAction
import com.advertisement.ads.model.AdvertisementStatus
import com.advertisement.ads.repository.AdvertisementRepository
import mu.KotlinLogging
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Clock
import java.util.*


private val logger = KotlinLogging.logger {}

@Component
class CreatedAdvertisementEditLifecycleProcessor(
    val advertisementRepository: AdvertisementRepository,
    val clock: Clock,
) : LifecycleProcessor {

    override fun process(advertisement: Advertisement, context: Map<String, Any>): Mono<Advertisement> {
        val userId = context.get("userId") as UUID
        if (advertisement.authorId != userId.toString()) {
            logger.error("User with id = $userId is not an author of advertisement with id = ${advertisement.id}")
            throw ApiForbiddenException("User is not an author")
        }
        return advertisementRepository.save(advertisement)
    }

    override fun getCase(): LifecycleProcessorCase {
        return LifecycleProcessorCase(AdvertisementStatus.CREATED, AdvertisementAction.EDITED)
    }
}