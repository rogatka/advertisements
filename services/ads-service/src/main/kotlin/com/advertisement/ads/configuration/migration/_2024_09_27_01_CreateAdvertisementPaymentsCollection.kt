package com.advertisement.ads.configuration.migration

import com.mongodb.reactivestreams.client.MongoDatabase
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync
import io.mongock.driver.mongodb.reactive.util.SubscriberSync


@ChangeUnit(id = "create-advertisement_payments-collection", order = "1", author = "Ilia_Klishin")
class _2024_09_27_01_CreateAdvertisementPaymentsCollection {

    @Execution
    fun execution(mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<Void> = MongoSubscriberSync()
        mongoDatabase.createCollection("advertisement_payments").subscribe(subscriber)
        subscriber.await()
    }

    @RollbackExecution
    fun rollbackExecution(mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<Void> = MongoSubscriberSync()
        mongoDatabase.getCollection("advertisement_payments").drop().subscribe(subscriber)
        subscriber.await()
    }
}
