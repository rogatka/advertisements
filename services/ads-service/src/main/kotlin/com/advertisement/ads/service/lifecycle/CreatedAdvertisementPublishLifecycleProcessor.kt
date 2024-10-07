package com.advertisement.ads.service.lifecycle

import com.advertisement.ads.configuration.AdvertisementProperties
import com.advertisement.ads.exception.ApiForbiddenException
import com.advertisement.ads.model.Advertisement
import com.advertisement.ads.model.AdvertisementAction
import com.advertisement.ads.model.AdvertisementHistory
import com.advertisement.ads.model.AdvertisementStatus
import com.advertisement.ads.repository.AdvertisementRepository
import mu.KotlinLogging
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Clock
import java.time.Instant
import java.util.*

private val logger = KotlinLogging.logger {}

@Component
class CreatedAdvertisementPublishLifecycleProcessor(
    val advertisementRepository: AdvertisementRepository,
    val clock: Clock,
    val advertisementProperties: AdvertisementProperties
) : LifecycleProcessor {

    override fun process(advertisement: Advertisement, context: Map<String, Any>): Mono<Advertisement> {
        val userId = context.get("userId") as UUID
        if (advertisement.authorId != userId.toString()) {
            logger.error("User with id = $userId is not an author of advertisement with id = ${advertisement.id}")
            throw ApiForbiddenException("User is not an author")
        }
        val advertisementCopy = advertisement.copy(
            status = AdvertisementStatus.ON_MODERATION,
            history = listOf(*advertisement.history.toTypedArray(),
                AdvertisementHistory(
                    AdvertisementStatus.ON_MODERATION,
                    Instant.now(clock),
                    AdvertisementHistory.StatusChangeInitiator(userId.toString(), "user")
                )
            )
        )
        return advertisementRepository.save(advertisementCopy)
    }

    override fun getCase(): LifecycleProcessorCase {
        return LifecycleProcessorCase(AdvertisementStatus.CREATED, AdvertisementAction.PUBLISH)
    }
}