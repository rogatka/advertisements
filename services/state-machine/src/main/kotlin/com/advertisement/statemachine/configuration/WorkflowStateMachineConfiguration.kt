package com.advertisement.statemachine.configuration

import com.advertisement.ads.model.AdvertisementAction
import com.advertisement.ads.model.AdvertisementStatus
import org.springframework.context.annotation.Configuration
import org.springframework.statemachine.StateContext
import org.springframework.statemachine.config.EnableStateMachineFactory
import org.springframework.statemachine.config.StateMachineConfigurerAdapter
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer
import org.springframework.statemachine.config.builders.StateMachineTransitionBuilder
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer
import org.springframework.statemachine.security.SecurityRule
import org.springframework.statemachine.transition.TransitionKind
import reactor.core.publisher.Mono
import java.util.*

@Configuration
@EnableStateMachineFactory
//@AutoConfiguration(after = [MongoRepositoriesAutoConfiguration::class])
//@ConditionalOnBean(name = ["stateMachineRuntimePersister"])
class WorkflowStateMachineConfiguration(
    val publishGuard: PublishGuard,
    val approveGuard: ApproveGuard,
    val publishAction: PublishAction,
    val rejectGuard: RejectGuard,
    val deactivateGuard: DeactivateGuard,
    val editGuard: EditGuard,
    val activateGuard: ActivateGuard,
    val stateMachineRuntimePersister: ReactiveMongoDBPersister<AdvertisementStatus, AdvertisementAction, String>
) : StateMachineConfigurerAdapter<AdvertisementStatus, AdvertisementAction>() {

    override fun configure(config: StateMachineConfigurationConfigurer<AdvertisementStatus, AdvertisementAction>) {
        config
            .withPersistence()
            .runtimePersister(stateMachineRuntimePersister)
        config
            .withConfiguration()
            .autoStartup(false)
            .listener(WorkFlowStateMachineListener())
    }

    override fun configure(states: StateMachineStateConfigurer<AdvertisementStatus, AdvertisementAction>) {
        states
            .withStates()
            .initial(AdvertisementStatus.CREATED)
            .states(EnumSet.allOf(AdvertisementStatus::class.java))
    }

    override fun configure(transitions: StateMachineTransitionConfigurer<AdvertisementStatus, AdvertisementAction>) {
        val transitionBuilder = transitions as StateMachineTransitionBuilder
        transitionBuilder
            .addTransition(
                AdvertisementStatus.CREATED,
                AdvertisementStatus.CREATED,
                null,
                AdvertisementAction.CREATE,
                null,
                null,
                mutableListOf(),
                null,
                TransitionKind.EXTERNAL,
                SecurityRule(),
                null
            )
        transitionBuilder
            .addTransition(
                AdvertisementStatus.CREATED,
                AdvertisementStatus.ON_MODERATION,
                null,
                AdvertisementAction.PUBLISH,
                null,
                null,
                mutableListOf(publishAction as java.util.function.Function<StateContext<AdvertisementStatus, AdvertisementAction>, Mono<Void>>),
                publishGuard,
                TransitionKind.EXTERNAL,
                SecurityRule(),
                null
            )
        transitionBuilder
            .addTransition(
                AdvertisementStatus.ON_MODERATION,
                AdvertisementStatus.REJECTED,
                null,
                AdvertisementAction.REJECT,
                null,
                null,
                mutableListOf(),
                rejectGuard,
                TransitionKind.EXTERNAL,
                SecurityRule(),
                null
            )
        transitionBuilder
            .addTransition(
                AdvertisementStatus.ON_MODERATION,
                AdvertisementStatus.PUBLISHED,
                null,
                AdvertisementAction.APPROVE,
                null,
                null,
                mutableListOf(),
                approveGuard,
                TransitionKind.EXTERNAL,
                SecurityRule(),
                null
            )
        transitionBuilder
            .addTransition(
                AdvertisementStatus.PUBLISHED,
                AdvertisementStatus.DEACTIVATED,
                null,
                AdvertisementAction.DEACTIVATE,
                null,
                null,
                mutableListOf(),
                deactivateGuard,
                TransitionKind.EXTERNAL,
                SecurityRule(),
                null
            )
        transitionBuilder
            .addTransition(
                AdvertisementStatus.PUBLISHED,
                AdvertisementStatus.ON_MODERATION,
                null,
                AdvertisementAction.EDITED,
                null,
                null,
                mutableListOf(),
                editGuard,
                TransitionKind.EXTERNAL,
                SecurityRule(),
                null
            )
        transitionBuilder
            .addTransition(
                AdvertisementStatus.DEACTIVATED,
                AdvertisementStatus.CREATED,
                null,
                AdvertisementAction.EDITED,
                null,
                null,
                mutableListOf(),
                editGuard,
                TransitionKind.EXTERNAL,
                SecurityRule(),
                null
            )
        transitionBuilder
            .addTransition(
                AdvertisementStatus.DEACTIVATED,
                AdvertisementStatus.PUBLISHED,
                null,
                AdvertisementAction.ACTIVATE,
                null,
                null,
                mutableListOf(),
                activateGuard,
                TransitionKind.EXTERNAL,
                SecurityRule(),
                null
            )
    }
}
