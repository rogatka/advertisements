package com.advertisement.statemachine.repository

import com.advertisement.ads.model.MongoDbRepositoryStateMachine
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MongoDbStateMachineRepository : ReactiveMongoRepository<MongoDbRepositoryStateMachine, String>
