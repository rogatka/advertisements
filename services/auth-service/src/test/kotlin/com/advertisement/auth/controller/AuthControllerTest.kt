package com.advertisement.auth.controller

import com.advertisement.auth.dto.response.AccessTokenResponse
import com.advertisement.auth.dto.request.LoginRequest
import com.advertisement.auth.dto.response.LoginResponse
import com.advertisement.auth.security.AuthenticationManager
import com.advertisement.auth.security.SecurityConfiguration
import com.advertisement.auth.security.SecurityContextRepository
import com.advertisement.auth.service.AuthService
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
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters.fromValue
import reactor.core.publisher.Mono
import java.time.*

private val instant: Instant = LocalDateTime.of(2024, Month.SEPTEMBER, 1, 0, 0, 0).toInstant(ZoneOffset.UTC)

@WebFluxTest(AuthController::class)
@Import(SecurityConfiguration::class, AuthControllerTest.TestConfig::class)
class AuthControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var clock: Clock

    @MockkBean
    private lateinit var authService: AuthService

    @MockkBean
    private lateinit var securityContextRepository: SecurityContextRepository

    @MockkBean
    private lateinit var authenticationManager: AuthenticationManager

    @Test
    @WithAnonymousUser
    fun `should get 401 when logout unauthorized user`() {
        // when-then
        webTestClient
            .mutateWith(csrf())
            .post().uri("/api/v1/auth/logout")
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    @WithAnonymousUser
    fun `should return 400 if incorrect phone when login`() {
        // given
        val loginRequest = LoginRequest("1234", "1234")
        val loginResponse = LoginResponse("access-token", "refresh-token")
        every { authService.login(loginRequest) } returns Mono.just(loginResponse)

        // when-then
        webTestClient
            .mutateWith(csrf())
            .post().uri("/api/v1/auth/login")
            .body(fromValue(loginRequest))
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.description").isEqualTo("{phone=must match \"\\d{5,20}\"}")
    }

    @Test
    @WithAnonymousUser
    fun `should get 200 with token when successfully logged in`() {
        // given
        val loginRequest = LoginRequest("12345", "1234")
        val loginResponse = LoginResponse("access-token", "refresh-token")
        every { authService.login(loginRequest) } returns Mono.just(loginResponse)

        // when-then
        webTestClient
            .mutateWith(csrf())
            .post().uri("/api/v1/auth/login")
            .body(fromValue(loginRequest))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.accessToken").isEqualTo(loginResponse.accessToken)
            .jsonPath("$.refreshToken").isEqualTo(loginResponse.refreshToken)

        verify { authService.login(loginRequest) }
    }

    @Test
    @WithAnonymousUser
    fun `should get 200 and new token`() {
        // given
        val refreshToken = "access-token"
        val loginResponse = AccessTokenResponse("refresh-token")
        every { authService.getAccessToken(refreshToken) } returns Mono.just(loginResponse)

        // when-then
        webTestClient
            .mutateWith(csrf())
            .post().uri("/api/v1/auth/access-token")
            .header("Refresh-Token", refreshToken)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.accessToken").isEqualTo(loginResponse.accessToken)

        verify { authService.getAccessToken(refreshToken) }
    }

    @TestConfiguration
    class TestConfig {

        @Bean
        fun clock(): Clock {
            return Clock.fixed(instant, ZoneId.systemDefault())
        }
    }
}