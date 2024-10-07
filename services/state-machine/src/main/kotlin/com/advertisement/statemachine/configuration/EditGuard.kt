package com.advertisement.statemachine.configuration

import com.advertisement.ads.exception.ApiInternalServerErrorException
import com.advertisement.ads.model.AdvertisementAction
import com.advertisement.ads.model.AdvertisementStatus
import com.advertisement.ads.repository.AdvertisementRepository
import org.springframework.statemachine.StateContext
import org.springframework.statemachine.guard.ReactiveGuard
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class EditGuard(
    val advertisementRepository: AdvertisementRepository
): ReactiveGuard<AdvertisementStatus, AdvertisementAction> {

    override fun apply(context: StateContext<AdvertisementStatus, AdvertisementAction>): Mono<Boolean> {
        val userId = context.getMessageHeader("userId") as UUID
        val advertisementId = context.getMessageHeader("advertisementId") as String
        return advertisementRepository.findById(advertisementId)
            .switchIfEmpty(Mono.error(ApiInternalServerErrorException("Advertisement not found")))
//            .flatMap { adv -> val isAuthor = adv.authorId != userId.toString()
//                context.extendedState.variables["isAuthor"] = false
//                Mono.just(isAuthor)
//            }
            .map { adv -> adv.authorId != userId.toString() }
    }
}