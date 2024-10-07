package com.advertisement.statemachine.configuration

import com.advertisement.ads.model.AdvertisementAction
import com.advertisement.ads.model.AdvertisementStatus
import mu.KotlinLogging
import org.springframework.statemachine.StateContext
import org.springframework.statemachine.action.ReactiveAction
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono


private val logger = KotlinLogging.logger {}

@Component
class PublishAction : ReactiveAction<AdvertisementStatus, AdvertisementAction> {

    /*override fun execute(context: StateContext<AdvertisementStatus, AdvertisementAction>) {
        logger.info("PublishAction")
    }*/

    override fun apply(context: StateContext<AdvertisementStatus, AdvertisementAction>?): Mono<Void> {
        logger.info("PublishAction: $context")
        return Mono.empty()
    }
}