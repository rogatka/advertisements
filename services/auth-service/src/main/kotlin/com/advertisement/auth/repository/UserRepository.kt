package com.advertisement.auth.repository

import com.advertisement.auth.model.User
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.bind
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.util.*

@Repository
class UserRepository (
    val databaseClient: DatabaseClient
) {
    fun save(user: User): Mono<User> {
        return databaseClient.sql("""
            insert into advertisement.users values
            (:id, :phone, :first_name, :last_name, :created_at)  
        """.trimIndent())
            .bind("id", user.id)
            .bind("phone", user.phone)
            .bind("first_name", user.firstName)
            .bind("last_name", user.lastName)
            .bind("created_at", user.createdAt)
            .filter{statement -> statement.returnGeneratedValues("id", "phone", "first_name", "last_name", "created_at")}
            .fetch()
            .first()
            .map { r -> User(r["id"] as UUID, r["phone"] as String, r["first_name"]?.toString(), r["last_name"]?.toString(), (r["created_at"] as OffsetDateTime).toInstant()) }
    }
}