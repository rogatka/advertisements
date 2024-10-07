package com.advertisement.auth.repository

import com.advertisement.auth.BaseIntegrationTest
import com.advertisement.auth.dto.OtpType
import com.advertisement.auth.model.Otp
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

@Import(OtpRepositoryIntegrationTest.TestConfig::class)
class OtpRepositoryIntegrationTest:BaseIntegrationTest() {

    @Autowired
    lateinit var otpRepository: OtpRepository

    @Autowired
    lateinit var databaseClient: DatabaseClient

    @Autowired
    lateinit var clock: Clock

    @BeforeEach
    fun setUp() {
        databaseClient.sql("delete from advertisement.otp").fetch().all().subscribe()
    }

    @Test
    fun `should correctly save new otp into DB`() {
        // given
        val otp = Otp(UUID.fromString("bd5fa336-d56d-45fa-842c-5ce4ab870922"), "1234567", "test".toByteArray(), OtpType.AUTH, Instant.now(clock))

        // when
        otpRepository.save(otp)
            .`as` { a -> StepVerifier.create(a) }
            .expectNextCount(1)
            .verifyComplete()

        // then
        databaseClient.sql("select * from advertisement.otp where id = :id")
            .bind("id", otp.id)
            .fetch()
            .all()
            .`as` { a -> StepVerifier.create(a) }
            .expectNextMatches{e ->
                (e["id"] as UUID == otp.id)
                .and(e["phone"] == otp.phone)
                .and(e["otp_hash"] != null)
                .and(e["type"] == OtpType.AUTH.name)
                .and((e["created_at"] as OffsetDateTime).toInstant() == otp.createdAt)
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