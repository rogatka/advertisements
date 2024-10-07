package com.advertisement.statemachine.configuration

import com.advertisement.ads.model.MongoDbRepositoryStateMachine
import com.advertisement.statemachine.repository.MongoDbStateMachineRepository
import org.springframework.statemachine.StateMachineContext
import org.springframework.statemachine.service.StateMachineSerialisationService

class MongoDbRepositoryStateMachinePersist<S, E> : RepositoryStateMachinePersist<S, E> {
    private val mongodbStateMachineRepository: MongoDbStateMachineRepository

    /**
     * Instantiates a new mongodb repository state machine persist.
     *
     * @param mongodbStateMachineRepository the mongodb state machine repository
     */
    constructor(mongodbStateMachineRepository: MongoDbStateMachineRepository) : super() {
        this.mongodbStateMachineRepository = mongodbStateMachineRepository
    }

    /**
     * Instantiates a new mongodb repository state machine persist.
     *
     * @param mongodbStateMachineRepository the mongodb state machine repository
     * @param serialisationService the serialisation service
     */
    constructor(
        mongodbStateMachineRepository: MongoDbStateMachineRepository,
        serialisationService: StateMachineSerialisationService<S, E>
    ) : super(serialisationService) {
        this.mongodbStateMachineRepository = mongodbStateMachineRepository
    }

    override val repository: MongoDbStateMachineRepository
        get() = mongodbStateMachineRepository

    override fun build(
        context: StateMachineContext<S, E>,
        contextObj: Any,
        serialisedContext: ByteArray
    ): MongoDbRepositoryStateMachine {
        val mongodbRepositoryStateMachine = MongoDbRepositoryStateMachine()
        mongodbRepositoryStateMachine.id = contextObj.toString()
        mongodbRepositoryStateMachine.machineId = context.id
        mongodbRepositoryStateMachine.state = context.state.toString()
        mongodbRepositoryStateMachine.stateMachineContext = serialisedContext
        return mongodbRepositoryStateMachine
    }
}
