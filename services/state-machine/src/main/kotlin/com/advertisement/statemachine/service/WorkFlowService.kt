package com.advertisement.statemachine.service

import com.advertisement.ads.model.Advertisement
import com.advertisement.ads.model.AdvertisementAction
import com.advertisement.ads.model.AdvertisementStatus
import org.springframework.statemachine.StateMachineEventResult
import reactor.core.publisher.Flux

fun interface WorkFlowService {
    fun executeTransition(
        advertisement: Advertisement,
        event: AdvertisementAction
    ): Flux<StateMachineEventResult<AdvertisementStatus, AdvertisementAction>>
}
