package com.advertisement.auth.repository

import com.advertisement.auth.BaseIntegrationTest
import com.advertisement.auth.dto.OtpType
import com.advertisement.auth.model.RefreshToken
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.r2dbc.core.DatabaseClient
import reactor.test.StepVerifier
import java.time.*
import java.util.*

private val instant: Instant = LocalDateTime.of(2024, Month.SEPTEMBER, 1, 0, 0, 0).toInstant(ZoneOffset.UTC)

class RefreshTokenRepositoryIntegrationTest: BaseIntegrationTest() {

    @Autowired
    lateinit var refreshTokenRepository: RefreshTokenRepository

    @Autowired
    lateinit var databaseClient: DatabaseClient

    @Autowired
    lateinit var clock: Clock

    @BeforeEach
    fun setUp() {
        databaseClient.sql("delete from advertisement.refresh_token").fetch().all().subscribe()
        databaseClient.sql("delete from advertisement.users").fetch().all().subscribe()
    }

    @Test
    fun `should successfully save refresh token into DB`() {
        //given
        val userId = UUID.fromString("6e63b2b5-12e5-41ae-8e89-c08dadf50c91")
        val refreshTokenId = UUID.fromString("04ee27a5-b1ba-49a1-b6a1-29dc84e37b28")

        databaseClient.sql("""insert into advertisement.users (id, phone, created_at) values
            (:id, :phone, :created_at)
        """.trimIndent())
            .bind("id", userId)
            .bind("phone", "1234578")
            .bind("created_at", Instant.now(clock))
            .fetch()
            .all()
            .subscribe();

        val refreshToken = RefreshToken(refreshTokenId, userId, "test".toByteArray(), false, Instant.now(clock))

        // when
        refreshTokenRepository.save(refreshToken)
            .`as` { a -> StepVerifier.create(a) }
            .expectNextCount(1)
            .verifyComplete()

        // then
        databaseClient.sql("select * from advertisement.refresh_token where id = :id")
            .bind("id", refreshTokenId)
            .fetch()
            .all()
            .`as` { a -> StepVerifier.create(a) }
            .expectNextMatches{e ->
                (e["id"] as UUID == refreshTokenId)
                    .and(e["user_id"] == userId)
                    .and(e["refresh_token_hash"] != null)
                    .and(e["revoked"] == refreshToken.revoked)
                    .and((e["created_at"] as OffsetDateTime).toInstant() == refreshToken.createdAt)
            }
            .verifyComplete()
    }

    @Test
    fun `should successfully revoke refresh token`() {
        //given
        val userId = UUID.fromString("fbfaba3a-471c-4dda-a469-47fcc03f602e")
        val refreshTokenId = UUID.fromString("a10b37ac-93a5-49f2-94e9-ea766c2b0225")
        val now = Instant.now(clock)

        databaseClient.sql("""insert into advertisement.users (id, phone, created_at) values
            (:id, :phone, :created_at)
        """.trimIndent())
            .bind("id", userId)
            .bind("phone", "12345789")
            .bind("created_at", now)
            .fetch()
            .all()
            .subscribe();

        databaseClient.sql("""insert into advertisement.refresh_token (id, user_id, refresh_token_hash, revoked, created_at) values
            (:id, :user_id, :refresh_token_hash, :revoked, :created_at)
        """.trimIndent())
            .bind("id", refreshTokenId)
            .bind("user_id", userId)
            .bind("refresh_token_hash", "test".toByteArray())
            .bind("revoked", false)
            .bind("created_at", now)
            .fetch()
            .all()
            .subscribe();

        // when
        refreshTokenRepository.revokeIfExistsByUserId(userId)
            .`as` { a -> StepVerifier.create(a) }
            .expectNextCount(1)
            .verifyComplete()

        // then
        databaseClient.sql("select * from advertisement.refresh_token where id = :id")
            .bind("id", refreshTokenId)
            .fetch()
            .all()
            .`as` { a -> StepVerifier.create(a) }
            .expectNextMatches{e ->
                (e["id"] as UUID == refreshTokenId)
                    .and(e["user_id"] == userId)
                    .and(e["refresh_token_hash"] != null)
                    .and(e["revoked"] == true)
                    .and((e["created_at"] as OffsetDateTime).toInstant() == now)
            }
            .verifyComplete()
    }

    @TestConfiguration
    class TestConfig {

        @Bean
        @Primary
        fun fixedClock(): Clock {
            return Clock.fixed(instant, ZoneId.systemDefault())
        }
    }
}