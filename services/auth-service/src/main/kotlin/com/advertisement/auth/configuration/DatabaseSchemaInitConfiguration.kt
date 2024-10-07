package com.advertisement.auth.configuration

import liquibase.change.DatabaseChange
import liquibase.integration.spring.SpringLiquibase
import mu.KotlinLogging
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import java.sql.SQLException

private val logger = KotlinLogging.logger {}

@Configuration
@ConditionalOnClass(SpringLiquibase::class, DatabaseChange::class)
@ConditionalOnProperty(prefix = "spring.liquibase", name = ["enabled"], matchIfMissing = true)
@AutoConfigureAfter(DataSourceAutoConfiguration::class,)
@Import(DatabaseSchemaInitConfiguration.SpringLiquibaseDependsOnPostProcessor::class)
class DatabaseSchemaInitConfiguration {

    @Component
    @ConditionalOnProperty(prefix = "spring.liquibase", name = ["enabled"], matchIfMissing = true)
    class SchemaInitBean @Autowired constructor(
        private val databaseClient: DatabaseClient,
        @param:Value("\${spring.liquibase.default-schema}")
        private val schemaName: String
    ) : InitializingBean {
        override fun afterPropertiesSet() {
            try {
                logger.info("Going to create DB schema '{}' if not exists.", schemaName)
                databaseClient.sql("create schema if not exists $schemaName").then().block()
            } catch (e: SQLException) {
                throw RuntimeException("Failed to create schema '$schemaName'", e)
            }
        }
    }

    @ConditionalOnBean(SchemaInitBean::class)
    internal class SpringLiquibaseDependsOnPostProcessor :
        AbstractDependsOnBeanFactoryPostProcessor(SpringLiquibase::class.java, SchemaInitBean::class.java)
}