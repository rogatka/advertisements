package com.advertisement.ads.configuration.migration

import com.mongodb.reactivestreams.client.MongoDatabase
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync
import io.mongock.driver.mongodb.reactive.util.SubscriberSync


@ChangeUnit(id = "create-state_machine-collection", order = "1", author = "Ilia_Klishin")
class _2024_09_20_01_CreateStateMachineCollection {

    @Execution
    fun execution(mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<Void> = MongoSubscriberSync()
        mongoDatabase.createCollection("state_machine").subscribe(subscriber)
        subscriber.await()
    }

    @RollbackExecution
    fun rollbackExecution(mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<Void> = MongoSubscriberSync()
        mongoDatabase.getCollection("state_machine").drop().subscribe(subscriber)
        subscriber.await()
    }
}
