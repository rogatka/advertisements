package com.advertisement.auth.repository

import com.advertisement.auth.model.UserRole
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.OffsetDateTime
import java.util.*

@Repository
class UserRoleRepository(
    val databaseClient: DatabaseClient
) {
    fun save(userRole: UserRole): Mono<UserRole> {
        return databaseClient.sql("""
            insert into advertisement.users_roles values
            (:id, :user_id, :role_id, :created_at)
        """.trimIndent())
            .bind("id", userRole.id)
            .bind("user_id", userRole.userId)
            .bind("role_id", userRole.roleId)
            .bind("created_at", userRole.createdAt)
            .filter{statement -> statement.returnGeneratedValues("id", "user_id", "role_id", "created_at")}
            .fetch()
            .first()
            .map { r -> UserRole(r["id"] as UUID, r["user_id"] as UUID, r["role_id"] as Long, (r["created_at"] as OffsetDateTime).toInstant()) }
    }
}