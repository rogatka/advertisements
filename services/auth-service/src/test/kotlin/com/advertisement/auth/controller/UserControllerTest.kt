package com.advertisement.auth.controller

import com.advertisement.auth.model.User
import com.advertisement.auth.security.AuthenticationManager
import com.advertisement.auth.security.SecurityConfiguration
import com.advertisement.auth.security.SecurityContextRepository
import com.advertisement.auth.service.UserService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import java.time.*
import java.util.*

private val instant: Instant = LocalDateTime.of(2024, Month.SEPTEMBER, 1, 0, 0, 0).toInstant(ZoneOffset.UTC)

@WebFluxTest(UserController::class)
@Import(SecurityConfiguration::class, UserControllerTest.TestConfig::class)
class UserControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var clock: Clock

    @MockkBean
    private lateinit var userService: UserService

    @MockkBean
    private lateinit var securityContextRepository: SecurityContextRepository

    @MockkBean
    private lateinit var authenticationManager: AuthenticationManager

    @Test
    @WithAnonymousUser
    fun `unauthorized user should get 401 when getting all users`() {
        // when-then
        webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .get().uri("/api/v1/users")
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    fun `'ADMINISTRATOR' should successfully get all users`() {
        // given
        every { userService.findAll() } returns Flux.fromIterable(listOf(User(UUID.randomUUID(), "12345", null, null, Instant.now(clock))))

        // when-then
        webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .mutateWith(SecurityMockServerConfigurers.mockUser().roles("ADMINISTRATOR"))
            .get().uri("/api/v1/users")
            .exchange()
            .expectStatus().isOk()

        verify { userService.findAll() }
    }

    @Test
    fun `'USER' should get 403 when trying to get all users`() {
        // when-then
        webTestClient
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .mutateWith(SecurityMockServerConfigurers.mockUser().roles("USER"))
            .get().uri("/api/v1/users")
            .exchange()
            .expectStatus().isForbidden();
    }

    @TestConfiguration
    class TestConfig {

        @Bean
        fun clock(): Clock {
            return Clock.fixed(instant, ZoneId.systemDefault())
        }
    }
}