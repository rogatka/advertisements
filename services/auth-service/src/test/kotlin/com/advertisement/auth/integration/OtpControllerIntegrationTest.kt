package com.advertisement.auth.integration

import com.advertisement.auth.BaseIntegrationTest
import com.advertisement.auth.dto.request.OtpRequest
import com.advertisement.auth.dto.OtpType
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.maciejwalkowiak.wiremock.spring.ConfigureWireMock
import com.maciejwalkowiak.wiremock.spring.EnableWireMock
import com.maciejwalkowiak.wiremock.spring.InjectWireMock
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters.fromValue
import reactor.core.publisher.Flux
import reactor.test.StepVerifier


@EnableWireMock(
    ConfigureWireMock(name = "sms-service", property = "sms-service.base-url")
)
class OtpControllerIntegrationTest: BaseIntegrationTest() {

    @InjectWireMock("sms-service")
    private lateinit var wiremock: WireMockServer

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var databaseClient: DatabaseClient

    @Test
    fun `should successfully send sms, save otp to DB and return 200`() {
        // given
        val otpRequest = OtpRequest("12345", OtpType.AUTH)

        wiremock.stubFor(post(WireMock.urlPathEqualTo("/api/token"))
            .withBasicAuth("advertisement-user", "advertisement-password")
            .withFormParam("grant_type", WireMock.equalTo("grant-type-mock"))
            .withFormParam("resource", WireMock.equalTo("resource-mock"))
            .willReturn(WireMock.okJson(getFileContentString("mocks/200-get-sms-service-token-response.json")))
        )

        wiremock.stubFor(post(WireMock.urlPathEqualTo("/api/sms"))
            .withHeader(HttpHeaders.AUTHORIZATION, WireMock.equalTo("Bearer sms-service-access-token"))
            .willReturn(WireMock.okJson(getFileContentString("mocks/200-send-sms-response.json")))
        )

        // when-then
        webTestClient
            .post().uri("/api/v1/otp")
            .header(HttpHeaders.ACCEPT_LANGUAGE, "EN")
            .body(fromValue(otpRequest))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.send").isEqualTo(false)
            .jsonPath("$.code").isNotEmpty()
            .jsonPath("$.message").value(containsString("Authorization code: "))

        Flux.from(databaseClient.sql("select * from advertisement.otp where phone = :phone")
            .bind("phone", otpRequest.phone)
            .fetch()
            .all())
            .`as` {a -> StepVerifier.create(a)}
            .consumeNextWith { r ->
                    assertThat(r["phone"]).isEqualTo(otpRequest.phone)
                    assertThat(r["type"]).isEqualTo(otpRequest.otpType.name)
            }
            .verifyComplete()
    }
}