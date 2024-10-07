package com.advertisement.statemachine.configuration

import com.advertisement.ads.exception.ApiNotFoundException
import com.advertisement.ads.model.AdvertisementAction
import com.advertisement.ads.model.AdvertisementStatus
import com.advertisement.ads.repository.AdvertisementRepository
import mu.KotlinLogging
import org.springframework.messaging.Message
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.state.State
import org.springframework.statemachine.support.StateMachineInterceptorAdapter
import org.springframework.statemachine.transition.Transition
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@Component
class WorkFlowStateMachineInterceptor(
    val advertisementRepository: AdvertisementRepository
) : StateMachineInterceptorAdapter<AdvertisementStatus, AdvertisementAction>() {

    override fun postStateChange(
        state: State<AdvertisementStatus, AdvertisementAction>,
        message: Message<AdvertisementAction>,
        transition: Transition<AdvertisementStatus, AdvertisementAction>,
        stateMachine: StateMachine<AdvertisementStatus, AdvertisementAction>,
        rootStateMachine: StateMachine<AdvertisementStatus, AdvertisementAction>
    ) {
        logger.info("WorkFlowStateMachineInterceptor")
        advertisementRepository.findById(rootStateMachine.id)
            .switchIfEmpty(Mono.error(ApiNotFoundException("Advertisement not found")))
            .flatMap { a ->
                a.let {
                    advertisementRepository.save(it.copy(status = rootStateMachine.state.id))
                }
            }
            .subscribe()
    }

    override fun stateMachineError(
        stateMachine: StateMachine<AdvertisementStatus, AdvertisementAction>,
        exception: Exception
    ): Exception {
        logger.error("{} StateMachine encountered error: [ message: {}]", "stateMachineError()", exception.message)
        return super.stateMachineError(stateMachine, exception)
    }
}
