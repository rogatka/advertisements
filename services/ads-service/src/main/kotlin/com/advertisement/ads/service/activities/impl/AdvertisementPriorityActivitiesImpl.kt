package com.advertisement.ads.service.activities.impl

import com.advertisement.ads.configuration.AdvertisementProperties
import com.advertisement.ads.exception.ApiNotFoundException
import com.advertisement.ads.model.*
import com.advertisement.ads.repository.AdvertisementPaymentRepository
import com.advertisement.ads.repository.AdvertisementRepository
import com.advertisement.ads.service.activities.AdvertisementPriorityActivities
import mu.KotlinLogging
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit

private val logger = KotlinLogging.logger {}

@Component
class AdvertisementPriorityActivitiesImpl(
    val advertisementRepository: AdvertisementRepository,
    val advertisementPaymentRepository: AdvertisementPaymentRepository,
    val advertisementProperties: AdvertisementProperties,
    val clock: Clock,
) : AdvertisementPriorityActivities {

    override fun increasePriority(advertisementId: String) {
        advertisementRepository.findById(advertisementId)
            .switchIfEmpty(Mono.error(ApiNotFoundException("Advertisement with id=${advertisementId} not found")))
            .flatMap { adv ->
                val now = Instant.now(clock)
                val advertisementCopy = adv.copy(
                    priorityInfo = PriorityInfo(
                        priority = PriorityType.HIGH,
                        paidDate = now,
                        expirationDate = now.plus(advertisementProperties.priorityDays.toLong(), ChronoUnit.DAYS),
                        history = listOf(*adv.priorityInfo.history.toTypedArray(),
                            PriorityInfo.PriorityHistory(PriorityType.HIGH, now, now.plus(advertisementProperties.priorityDays.toLong(), ChronoUnit.DAYS))
                        )
                    )
                )
                advertisementRepository.save(advertisementCopy)
            }
            .flatMap { _ -> advertisementPaymentRepository.findByAdvertisementId(advertisementId)}
            .switchIfEmpty(Mono.error(ApiNotFoundException("AdvertisementPayment with advertisementId=${advertisementId} not found")))
            .flatMap { ap ->
                val now = Instant.now(clock)
                val advertisementPaymentCopy = ap.copy(
                    status = PaymentStatus.COMPLETED,
                    updateDate = now,
                    statusHistory = listOf(*ap.statusHistory.toTypedArray(),
                        PaymentStatusHistory(PaymentStatus.COMPLETED, now)
                    )
                )
                advertisementPaymentRepository.save(advertisementPaymentCopy)
            }
            .block()
    }

    override fun decreasePriority(advertisementId: String) {
        advertisementRepository.findById(advertisementId)
            .switchIfEmpty(Mono.error(ApiNotFoundException("Advertisement with id=${advertisementId} not found")))
            .flatMap { adv ->
                if (adv.priorityInfo.priority != PriorityType.NORMAL) {
                val advertisementCopy = adv.copy(
                    priorityInfo = PriorityInfo(
                        priority = PriorityType.NORMAL,
                        paidDate = null,
                        expirationDate = null,
                        history = listOf(*adv.priorityInfo.history.toTypedArray(),
                            PriorityInfo.PriorityHistory(PriorityType.NORMAL, null, null)
                        )
                    )
                )
                advertisementRepository.save(advertisementCopy)
                } else {
                    Mono.just(adv)
                }
            }
            .flatMap { _ -> advertisementPaymentRepository.findByAdvertisementId(advertisementId)}
            .switchIfEmpty(Mono.error(ApiNotFoundException("AdvertisementPayment with advertisementId=${advertisementId} not found")))
            .flatMap { ap ->
                val now = Instant.now(clock)
                val advertisementPaymentCopy = ap.copy(
                    status = PaymentStatus.FAILED,
                    updateDate = now,
                    statusHistory = listOf(*ap.statusHistory.toTypedArray(),
                        PaymentStatusHistory(PaymentStatus.FAILED, now)
                    )
                )
                advertisementPaymentRepository.save(advertisementPaymentCopy)
            }
            .block()
    }
}
