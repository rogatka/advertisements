package com.advertisement.ads.configuration.migration

import com.advertisement.ads.model.City
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mongodb.MongoClientSettings
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertManyResult
import com.mongodb.reactivestreams.client.ClientSession
import com.mongodb.reactivestreams.client.MongoDatabase
import io.mongock.api.annotations.*
import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync
import io.mongock.driver.mongodb.reactive.util.SubscriberSync
import org.bson.Document
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


@ChangeUnit(id = "create-cities-collection", order = "1", author = "Ilia_Klishin")
class _2024_09_12_02_CreateCitiesCollection {

    @BeforeExecution
    fun beforeExecution(mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<Void> = MongoSubscriberSync()
        mongoDatabase.createCollection("cities").subscribe(subscriber)
        subscriber.await()
    }

    @RollbackBeforeExecution
    fun rollbackBeforeExecution(mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<Void> = MongoSubscriberSync()
        mongoDatabase.getCollection("cities").drop().subscribe(subscriber)
        subscriber.await()
    }

    @Execution
    fun execution(clientSession: ClientSession, mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<InsertManyResult> = MongoSubscriberSync()
        mongoDatabase.getCollection("cities", City::class.java)
            .withCodecRegistry(codecRegistry())
            .insertMany(
                clientSession,
                getCitiesFromFile()
            )
            .subscribe(subscriber)
        subscriber.await()
    }

    @RollbackExecution
    fun rollbackExecution(clientSession: ClientSession, mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<DeleteResult> = MongoSubscriberSync()
        mongoDatabase
            .getCollection("cities", City::class.java)
            .deleteMany(clientSession, Document())
            .subscribe(subscriber)
        subscriber.await()
    }

    private fun getCitiesFromFile(): List<City> {
        val resource = this.javaClass.classLoader.getResource("cities/russian-cities.json")
        Objects.requireNonNull(resource)

        @JsonIgnoreProperties(ignoreUnknown = true) data class CityData (val name: String)

        return jacksonObjectMapper()
            .readValue<List<CityData>>(Files.readString(Paths.get(resource.toURI())))
            .map { c -> City(null, "Russia", c.name) }
    }

    private fun codecRegistry(): CodecRegistry {
        val pojoCodecRegistry: CodecRegistry = fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build())
        )
        return pojoCodecRegistry
    }
}
