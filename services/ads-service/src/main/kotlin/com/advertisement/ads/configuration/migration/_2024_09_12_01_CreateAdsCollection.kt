package com.advertisement.ads.configuration.migration

import com.mongodb.reactivestreams.client.MongoDatabase
import io.mongock.api.annotations.*
import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync
import io.mongock.driver.mongodb.reactive.util.SubscriberSync


@ChangeUnit(id = "create-ads-collection", order = "1", author = "Ilia_Klishin")
class _2024_09_12_01_CreateAdsCollection {

    @Execution
    fun execution(mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<Void> = MongoSubscriberSync()
        mongoDatabase.createCollection("ads").subscribe(subscriber)
        subscriber.await()
    }

    @RollbackExecution
    fun rollbackExecution(mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<Void> = MongoSubscriberSync()
        mongoDatabase.getCollection("ads").drop().subscribe(subscriber)
        subscriber.await()
    }

    /*@RollbackBeforeExecution
    fun rollbackBeforeExecution(mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<Void> = MongoSubscriberSync()
        mongoDatabase.getCollection("ads").drop().subscribe(subscriber)
        subscriber.await()
    }

    @Execution
    fun execution(clientSession: ClientSession, mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<InsertManyResult> = MongoSubscriberSync()
        mongoDatabase.getCollection("ads", Advertisement::class.java)
            .withCodecRegistry(codecRegistry())
            .insertMany(
                clientSession, IntStream.range(0, 10)
                    .mapToObj{ i -> getClient(i) }
                    .collect(Collectors.toList())
            )
            .subscribe(subscriber)

        val result: InsertManyResult = subscriber.getFirst()
        logger.info("_2024_09_12_01_CreateAdsCollection.execution wasAcknowledged: {}", result.wasAcknowledged())
        result.insertedIds
            .forEach { (key: Int?, value: BsonValue?) ->
                logger.info(
                    "update id[{}] : {}",
                    key,
                    value
                )
            }
    }

    @RollbackExecution
    fun rollbackExecution(clientSession: ClientSession, mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<DeleteResult> = MongoSubscriberSync()
        mongoDatabase
            .getCollection("ads", Advertisement::class.java)
            .deleteMany(clientSession, Document())
            .subscribe(subscriber)
        val result: DeleteResult = subscriber.getFirst()
    }

    private fun getClient(i: Int): Advertisement {
        return Advertisement(null, "title-$i")
    }*/

/*    private fun codecRegistry(): CodecRegistry {
        val pojoCodecRegistry: CodecRegistry = fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build())
        )
        return pojoCodecRegistry
    }*/
}
