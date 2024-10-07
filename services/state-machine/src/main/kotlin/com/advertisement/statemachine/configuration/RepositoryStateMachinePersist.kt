package com.advertisement.statemachine.configuration

import com.advertisement.ads.model.MongoDbRepositoryStateMachine
import com.advertisement.statemachine.repository.MongoDbStateMachineRepository
import mu.KotlinLogging
import org.springframework.statemachine.StateMachineContext
import org.springframework.statemachine.kryo.KryoStateMachineSerialisationService
import org.springframework.statemachine.service.StateMachineSerialisationService
import org.springframework.statemachine.support.DefaultStateMachineContext
import org.springframework.util.Assert
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

abstract class RepositoryStateMachinePersist<S, E> : StateMachinePersist<S, E, Any> {
    private val serialisationService: StateMachineSerialisationService<S, E>

    /**
     * Instantiates a new repository state machine persist.
     */
    protected constructor() {
        this.serialisationService = KryoStateMachineSerialisationService()
    }

    /**
     * Instantiates a new repository state machine persist.
     *
     * @param serialisationService the serialisation service
     */
    protected constructor(serialisationService: StateMachineSerialisationService<S, E>) {
        Assert.notNull(serialisationService, "'serialisationService' must be set")
        this.serialisationService = serialisationService
    }

    @Throws(Exception::class)
    override fun write(context: StateMachineContext<S, E>, contextObj: Any) {
        if (logger.isDebugEnabled) {
            logger.debug("Persisting context $context using contextObj $contextObj")
        }
        val build = build(context, contextObj, serialisationService.serialiseStateMachineContext(context))
        repository.save(build).subscribe()
    }

    @Throws(Exception::class)
    override fun read(contextObj: Any): Mono<StateMachineContext<S, E>> {
        if (logger.isDebugEnabled) {
            logger.debug("Reading context using contextObj: $contextObj")
        }
        return repository.findById(contextObj.toString())
            .flatMap { f: MongoDbRepositoryStateMachine ->
                try {
                    return@flatMap Mono.just<StateMachineContext<S, E>>(
                        serialisationService.deserialiseStateMachineContext(
                            f.stateMachineContext
                        )
                    )
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
            .filter { context: StateMachineContext<S, E>? -> context != null && context.childs != null && context.childs.isEmpty() && context.childReferences != null }
            .flatMap { c: StateMachineContext<S, E> ->
                val contexts: MutableList<StateMachineContext<S, E>> = ArrayList()
                for (childRef in c.childReferences) {
                    repository.findById(childRef)
                        .flatMap { con: MongoDbRepositoryStateMachine ->
                            try {
                                contexts.add(serialisationService.deserialiseStateMachineContext(con.stateMachineContext))
                            } catch (e: Exception) {
                                throw RuntimeException(e)
                            }
                            Mono.just(con)
                        }
                }
                Mono.just(
                    DefaultStateMachineContext(
                        contexts, c.state, c.event,
                        c.eventHeaders, c.extendedState, c.historyStates,
                        c.id
                    )
                )
            }
    }

    protected abstract val repository: MongoDbStateMachineRepository
        /**
         * Gets the repository.
         *
         * @return the repository
         */
        get

    protected abstract fun build(
        context: StateMachineContext<S, E>,
        contextObj: Any,
        serialisedContext: ByteArray
    ): MongoDbRepositoryStateMachine
}
