package com.advertisement.statemachine.configuration

import com.advertisement.ads.model.AdvertisementAction
import com.advertisement.ads.model.AdvertisementStatus
import mu.KotlinLogging
import org.springframework.messaging.Message
import org.springframework.statemachine.StateContext
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.listener.StateMachineListenerAdapter
import org.springframework.statemachine.state.State
import org.springframework.statemachine.transition.Transition
import java.lang.Exception

private val logger = KotlinLogging.logger {}

class WorkFlowStateMachineListener : StateMachineListenerAdapter<AdvertisementStatus, AdvertisementAction>() {

    override fun stateChanged(from: State<AdvertisementStatus, AdvertisementAction>?, to: State<AdvertisementStatus, AdvertisementAction>?) {
        logger.info("State changed from ${from?.id?.name} to ${to?.id?.name}")
        super.stateChanged(from, to)
    }

    override fun eventNotAccepted(event: Message<AdvertisementAction>) {
        logger.info("State change failed: ${event.payload}")
        super.eventNotAccepted(event)
    }

    override fun stateEntered(state: State<AdvertisementStatus, AdvertisementAction>?) {
        logger.info("stateEntered: {}", state)
        super.stateEntered(state)
    }

    override fun stateExited(state: State<AdvertisementStatus, AdvertisementAction>?) {
        logger.info("stateExited: {}", state)
        super.stateExited(state)
    }

    override fun transition(transition: Transition<AdvertisementStatus, AdvertisementAction>?) {
        logger.info("transition: {}", transition)
        super.transition(transition)
    }

    override fun transitionStarted(transition: Transition<AdvertisementStatus, AdvertisementAction>?) {
        logger.info("transitionStarted: {}", transition)
        super.transitionStarted(transition)
    }

    override fun transitionEnded(transition: Transition<AdvertisementStatus, AdvertisementAction>?) {
        logger.info("transitionEnded: {}", transition)
        super.transitionEnded(transition)
    }

    override fun stateMachineStarted(stateMachine: StateMachine<AdvertisementStatus, AdvertisementAction>?) {
        logger.info("stateMachineStarted: {}", stateMachine)
        super.stateMachineStarted(stateMachine)
    }

    override fun stateMachineStopped(stateMachine: StateMachine<AdvertisementStatus, AdvertisementAction>?) {
        logger.info("stateMachineStopped: {}", stateMachine)
        super.stateMachineStopped(stateMachine)
    }

    override fun stateMachineError(
        stateMachine: StateMachine<AdvertisementStatus, AdvertisementAction>?,
        exception: Exception?
    ) {
        logger.info("stateMachineError: {}, {}", stateMachine, exception?.message)
        super.stateMachineError(stateMachine, exception)
    }

    override fun extendedStateChanged(key: Any?, value: Any?) {
        logger.info("extendedStateChanged: {}, {}", key, value)
        super.extendedStateChanged(key, value)
    }

    override fun stateContext(stateContext: StateContext<AdvertisementStatus, AdvertisementAction>?) {
        logger.info("stateContext: {}", stateContext)
        super.stateContext(stateContext)
    }
}