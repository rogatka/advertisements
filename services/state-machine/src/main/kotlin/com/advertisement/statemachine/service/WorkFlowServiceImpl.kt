package com.advertisement.statemachine.service

import com.advertisement.statemachine.configuration.WorkFlowStateMachineInterceptor
import com.advertisement.ads.dto.UserInfo
import com.advertisement.ads.exception.ApiForbiddenException
import com.advertisement.ads.exception.ApiInternalServerErrorException
import com.advertisement.ads.model.Advertisement
import com.advertisement.ads.model.AdvertisementAction
import com.advertisement.ads.model.AdvertisementStatus
import mu.KotlinLogging
import org.springframework.messaging.support.MessageBuilder
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.StateMachineEventResult
import org.springframework.statemachine.StateMachinePersist
import org.springframework.statemachine.access.StateMachineAccess
import org.springframework.statemachine.config.StateMachineFactory
import org.springframework.statemachine.service.DefaultStateMachineService
import org.springframework.statemachine.service.StateMachineService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


private val logger = KotlinLogging.logger {}

@Service
class WorkFlowServiceImpl (
    val stateMachineInterceptor: WorkFlowStateMachineInterceptor,
    stateMachineFactory: StateMachineFactory<AdvertisementStatus, AdvertisementAction>,
    mongoDbRepositoryStateMachinePersist: StateMachinePersist<AdvertisementStatus, AdvertisementAction, String>
): WorkFlowService {

    private final val stateMachineService: StateMachineService<AdvertisementStatus, AdvertisementAction> = DefaultStateMachineService(stateMachineFactory, mongoDbRepositoryStateMachinePersist)

    override fun executeTransition(advertisement: Advertisement, event: AdvertisementAction): Flux<StateMachineEventResult<AdvertisementStatus, AdvertisementAction>> {
        val sm: StateMachine<AdvertisementStatus, AdvertisementAction> = stateMachineService.acquireStateMachine(advertisement.id)
        sm.stateMachineAccessor
            .doWithAllRegions { sma: StateMachineAccess<AdvertisementStatus, AdvertisementAction> ->
                sma.addStateMachineInterceptor(stateMachineInterceptor)
            }
        return ReactiveSecurityContextHolder.getContext()
            .map { c -> c.authentication }
            .map { a -> a.principal as UserInfo }
            .switchIfEmpty(Mono.error(ApiInternalServerErrorException("User not found in context")))
            .flatMapMany { user ->  val message = MessageBuilder
                .withPayload(event)
                .setHeader("advertisementId", advertisement.id)
                .setHeader("userId", user.id)
                .setHeader("userRoles", user.roles)
                .build()
                sm.sendEvent(Mono.just(message))
            }
            .flatMap{r -> if (r.resultType == StateMachineEventResult.ResultType.DENIED) {
                Flux.error(ApiForbiddenException("Forbidden action"))
            } else {
                Flux.just(r)
            }}
            .switchIfEmpty(Flux.error(ApiInternalServerErrorException("Unknown error")))
    }
}
