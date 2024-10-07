package com.advertisement.auth.controller

import com.advertisement.auth.dto.request.OtpRequest
import com.advertisement.auth.dto.OtpType
import com.advertisement.auth.dto.response.SmsSendResponse
import com.advertisement.auth.security.AuthenticationManager
import com.advertisement.auth.security.SecurityConfiguration
import com.advertisement.auth.security.SecurityContextRepository
import com.advertisement.auth.service.OtpService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters.fromValue
import reactor.core.publisher.Mono
import java.time.*
import java.util.*

private val instant: Instant = LocalDateTime.of(2024, Month.SEPTEMBER, 1, 0, 0, 0).toInstant(ZoneOffset.UTC)

@WebFluxTest(OtpController::class)
@Import(SecurityConfiguration::class, OtpControllerTest.TestConfig::class)
class OtpControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var clock: Clock

    @MockkBean
    private lateinit var otpService: OtpService

    @MockkBean
    private lateinit var securityContextRepository: SecurityContextRepository

    @MockkBean
    private lateinit var authenticationManager: AuthenticationManager

    @Test
    @WithAnonymousUser
    fun `should return 400 when incorrect phone`() {
        // given
        val otpRequest = OtpRequest("1234", OtpType.AUTH)
        val smsSendResponse = SmsSendResponse(true, "1234", "test")
        every { otpService.requestOtp(otpRequest, Locale.of("RU")) } returns Mono.just(smsSendResponse)

        // when-then
        webTestClient
            .mutateWith(csrf())
            .post().uri("/api/v1/otp")
            .body(fromValue(otpRequest))
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.description").isEqualTo("{phone=must match \"\\d{5,20}\"}")
    }

    @Test
    @WithAnonymousUser
    fun `should successfully send sms and return 200`() {
        // given
        val otpRequest = OtpRequest("12345", OtpType.AUTH)
        val smsSendResponse = SmsSendResponse(true, "12345", "test")
        every { otpService.requestOtp(otpRequest, Locale.of("RU")) } returns Mono.just(smsSendResponse)

        // when-then
        webTestClient
            .mutateWith(csrf())
            .post().uri("/api/v1/otp")
            .body(fromValue(otpRequest))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.send").isEqualTo(smsSendResponse.send)
            .jsonPath("$.code").isEqualTo(smsSendResponse.code!!)
            .jsonPath("$.message").isEqualTo(smsSendResponse.message)

        verify { otpService.requestOtp(otpRequest, Locale.of("RU")) }
    }

    @Test
    @WithAnonymousUser
    fun `should successfully send sms and change locale accordingly and return 200`() {
        // given
        val locale = "EN"
        val otpRequest = OtpRequest("12345", OtpType.AUTH)
        val smsSendResponse = SmsSendResponse(true, "12345", "test")
        every { otpService.requestOtp(any(), any()) } returns Mono.just(smsSendResponse)

        // when-then
        webTestClient
            .mutateWith(csrf())
            .post().uri("/api/v1/otp")
            .header(HttpHeaders.ACCEPT_LANGUAGE, "EN")
            .body(fromValue(otpRequest))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.send").isEqualTo(smsSendResponse.send)
            .jsonPath("$.code").isEqualTo(smsSendResponse.code!!)
            .jsonPath("$.message").isEqualTo(smsSendResponse.message)

        verify { otpService.requestOtp(eq(otpRequest), eq(Locale.of(locale))) }
    }

    @TestConfiguration
    class TestConfig {

        @Bean
        fun clock(): Clock {
            return Clock.fixed(instant, ZoneId.systemDefault())
        }
    }
}