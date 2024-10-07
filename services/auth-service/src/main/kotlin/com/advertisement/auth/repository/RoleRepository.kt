package com.advertisement.auth.repository

import com.advertisement.auth.model.Role
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.util.*

@Repository
class RoleRepository(
    val databaseClient: DatabaseClient
) {
    fun findFirstByNameEquals(name: String): Mono<Role> {
        return databaseClient.sql("""select * from advertisement.roles
            where name = :name
            limit 1
        """.trimIndent())
            .bind("name", name)
            .fetch()
            .first()
            .map { r -> Role(r["id"] as Long, r["name"] as String, (r["created_at"] as OffsetDateTime).toInstant()) }
    }
}