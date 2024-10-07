package com.advertisement.auth.repository

import com.advertisement.auth.BaseIntegrationTest
import com.advertisement.auth.model.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.r2dbc.core.DatabaseClient
import reactor.test.StepVerifier
import java.time.*
import java.util.*

private val instant: Instant = LocalDateTime.of(2024, Month.SEPTEMBER, 1, 0, 0, 0).toInstant(ZoneOffset.UTC)

@Import(UserRepositoryIntegrationTest.TestConfig::class)
class UserRepositoryIntegrationTest: BaseIntegrationTest() {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var databaseClient: DatabaseClient

    @Autowired
    lateinit var clock: Clock

    @BeforeEach
    fun setUp() {
        databaseClient.sql("delete from advertisement.users").fetch().all().subscribe()
    }

    @Test
    fun `should correctly save new user into DB`() {
        // given
        val user = User(UUID.fromString("4cffb7be-a5c0-42d7-badb-edd378e8e5e2"), "1234567", null, null, Instant.now(clock))

        // when
        userRepository.save(user)
            .`as` { a -> StepVerifier.create(a) }
            .expectNextCount(1)
            .verifyComplete()

        // then
        databaseClient.sql("select * from advertisement.users where id = :id")
            .bind("id", user.id)
            .fetch()
            .all()
            .`as` { a -> StepVerifier.create(a) }
            .expectNextMatches{e ->
                (e["id"] as UUID == user.id)
                    .and(e["phone"] == user.phone)
                    .and((e["created_at"] as OffsetDateTime).toInstant() == user.createdAt)
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