package com.advertisement.statemachine.configuration

import com.advertisement.ads.model.AdvertisementAction
import com.advertisement.ads.model.AdvertisementStatus
import com.advertisement.ads.repository.AdvertisementRepository
import mu.KotlinLogging
import org.springframework.statemachine.StateContext
import org.springframework.statemachine.guard.ReactiveGuard
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@Component
class ApproveGuard(
    val advertisementRepository: AdvertisementRepository
): ReactiveGuard<AdvertisementStatus, AdvertisementAction> {

    override fun apply(context: StateContext<AdvertisementStatus, AdvertisementAction>): Mono<Boolean> {
        val userRoles = context.getMessageHeader("userRoles") as List<String>
        val result = "MODERATOR" in userRoles
        logger.info("ApproveGuard: $result. Roles: $userRoles")
        return Mono.just(result)
    }
}