package com.advertisement.ads.configuration

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver
import io.mongock.runner.springboot.MongockSpringboot
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner
import mu.KotlinLogging
import org.bson.UuidRepresentation
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.ReactiveMongoTransactionManager
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement


private val logger = KotlinLogging.logger {}

@Configuration
@EnableTransactionManagement
@EnableReactiveMongoRepositories(basePackages = ["com.advertisement.ads.repository"])
class MongoConfiguration(
    val mongoProperties: MongoProperties
) : AbstractReactiveMongoConfiguration() {

    override fun getDatabaseName(): String = mongoProperties.database

    @Bean
    fun transactionManager(dbFactory: ReactiveMongoDatabaseFactory): ReactiveMongoTransactionManager {
        return ReactiveMongoTransactionManager(dbFactory)
    }

    @Bean
    fun getBuilder(reactiveMongoClient: MongoClient, context: ApplicationContext): MongockInitializingBeanRunner {
        return MongockSpringboot.builder()
            .setDriver(MongoReactiveDriver.withDefaultLock(reactiveMongoClient, databaseName))
            .addMigrationScanPackage("com.advertisement.ads.configuration.migration")
            .setSpringContext(context)
            .setTransactionEnabled(true)
            .buildInitializingBeanRunner()
    }

    @Bean
    override fun reactiveMongoClient(): MongoClient {
        val codecRegistry = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
        )

        return MongoClients.create(
            MongoClientSettings.builder()
                .applyConnectionString(ConnectionString(mongoProperties.uri))
                .codecRegistry(codecRegistry)
                .uuidRepresentation(UuidRepresentation.JAVA_LEGACY)
                .build()
        )
    }

/*    @Bean
    fun mongoClient(): MongoClient {
        val codecRegistry = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
        )

        return MongoClients.create(
            MongoClientSettings.builder()
                .applyConnectionString(ConnectionString(mongoProperties.uri))
                .codecRegistry(codecRegistry)
                .build()
        )
    }*/
}