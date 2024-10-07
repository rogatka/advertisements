package com.advertisement.statemachine.configuration

import com.advertisement.statemachine.repository.MongoDbStateMachineRepository
import mu.KotlinLogging
import org.springframework.statemachine.StateMachineContext
import org.springframework.statemachine.persist.AbstractPersistingStateMachineInterceptor
import org.springframework.statemachine.persist.StateMachineRuntimePersister
import org.springframework.statemachine.support.StateMachineInterceptor
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class ReactiveMongoDBPersister<S, E, T:Any>(
    mongoDbStateMachineRepository: MongoDbStateMachineRepository
) : AbstractPersistingStateMachineInterceptor<S, E, T>(), StateMachineRuntimePersister<S, E, T> {

    private final val persist: RepositoryStateMachinePersist<S,E> = MongoDbRepositoryStateMachinePersist(mongoDbStateMachineRepository)

    override fun getInterceptor(): StateMachineInterceptor<S, E> {
        return this
    }

    override fun write(context: StateMachineContext<S, E>, contextObj: T) {
        persist.write(context, contextObj)
    }

    override fun read(contextObj: T): StateMachineContext<S, E>? {
        val stateMachineContext = persist.read(contextObj).block()
        logger.info("stateMachineContext = $stateMachineContext")
        return stateMachineContext
    }

}