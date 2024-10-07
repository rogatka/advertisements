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
import java.time.temporal.ChronoUnit
import java.util.*

private val logger = KotlinLogging.logger {}

@Component
class OnModerationAdvertisementApproveLifecycleProcessor(
    val advertisementRepository: AdvertisementRepository,
    val clock: Clock,
    val advertisementProperties: AdvertisementProperties
) : LifecycleProcessor {

    override fun process(advertisement: Advertisement, context: Map<String, Any>): Mono<Advertisement> {
        val userId = context.get("userId") as UUID
        val userRoles = context["userRoles"] as List<String>
        val now = Instant.now(clock)
        if ("MODERATOR" !in userRoles) {
            logger.error("User with id = $userId is not a moderator")
            throw ApiForbiddenException("User is not a moderator")
        }
        val advertisementCopy = advertisement.copy(
            status = AdvertisementStatus.PUBLISHED,
            expirationDate = now.plus(advertisementProperties.durationDays.toLong(), ChronoUnit.DAYS),
            publicationDate = now,
            history = listOf(*advertisement.history.toTypedArray(),
                AdvertisementHistory(
                    AdvertisementStatus.PUBLISHED,
                    Instant.now(clock),
                    AdvertisementHistory.StatusChangeInitiator(userId.toString(), "user")
                )
            )
        )
        return advertisementRepository.save(advertisementCopy)
    }

    override fun getCase(): LifecycleProcessorCase {
        return LifecycleProcessorCase(AdvertisementStatus.ON_MODERATION, AdvertisementAction.APPROVE)
    }
}