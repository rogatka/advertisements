package com.advertisement.auth.repository

import com.advertisement.auth.BaseIntegrationTest
import com.advertisement.auth.model.UserRole
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

@Import(UserRoleRepositoryIntegrationTest.TestConfig::class)
class UserRoleRepositoryIntegrationTest: BaseIntegrationTest() {

    @Autowired
    lateinit var userRoleRepository: UserRoleRepository

    @Autowired
    lateinit var databaseClient: DatabaseClient

    @Autowired
    lateinit var clock: Clock

    @BeforeEach
    fun setUp() {
        databaseClient.sql("delete from advertisement.users_roles").fetch().all().subscribe()
        databaseClient.sql("delete from advertisement.users").fetch().all().subscribe()
    }

    @Test
    fun `should correctly save new user into DB`() {
        // given
        val userId = UUID.fromString("2687189c-c9ba-45ad-ba22-32d361b3fa80")
        val userRoleId = UUID.fromString("65192505-5fb0-4b30-8cbd-2cd29a41a622")

        databaseClient.sql("""insert into advertisement.users (id, phone, created_at) values
            (:id, :phone, :created_at)
        """.trimIndent())
            .bind("id", userId)
            .bind("phone", "123456")
            .bind("created_at", Instant.now(clock))
            .fetch()
            .all()
            .subscribe();

        val userRole = UserRole(userRoleId, userId, 1, Instant.now(clock))

        // when
        userRoleRepository.save(userRole)
            .`as` { a -> StepVerifier.create(a) }
            .expectNextCount(1)
            .verifyComplete()

        // then
        databaseClient.sql("select * from advertisement.users_roles where id = :id")
            .bind("id", userRole.id)
            .fetch()
            .all()
            .`as` { a -> StepVerifier.create(a) }
            .expectNextMatches{e ->
                (e["id"] as UUID == userRole.id)
                    .and(e["user_id"] == userId)
                    .and(e["role_id"] == userRole.roleId)
                    .and((e["created_at"] as OffsetDateTime).toInstant() == userRole.createdAt)
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