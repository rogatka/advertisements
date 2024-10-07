package com.advertisement.auth.repository

import com.advertisement.auth.model.RefreshToken
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.util.*

@Repository
class RefreshTokenRepository(
    val databaseClient: DatabaseClient
) {

    fun save(refreshToken: RefreshToken): Mono<RefreshToken> {
        return databaseClient.sql("""
            INSERT INTO advertisement.refresh_token (id, user_id, refresh_token_hash, revoked, created_at) VALUES
            (:id, :userId, :refresh_token_hash, :revoked, :created_at)""".trimIndent())
            .bind("id", refreshToken.id)
            .bind("userId", refreshToken.userId)
            .bind("refresh_token_hash", refreshToken.hash)
            .bind("revoked", refreshToken.revoked)
            .bind("created_at", refreshToken.createdAt)
            .filter{statement -> statement.returnGeneratedValues("id", "user_id", "refresh_token_hash", "revoked", "created_at")}
            .fetch()
            .first()
            .map { r -> RefreshToken(r["id"] as UUID, r["user_id"] as UUID, refreshToken.hash, r["revoked"] as Boolean, (r["created_at"] as OffsetDateTime).toInstant())}
    }

    fun revokeIfExistsByUserId(userId: UUID): Mono<Long> {
        return databaseClient.sql("""
            update advertisement.refresh_token set revoked = 'true'
            where user_id = :userId
        """.trimIndent())
            .bind("userId", userId)
            .fetch()
            .rowsUpdated()
    }
}