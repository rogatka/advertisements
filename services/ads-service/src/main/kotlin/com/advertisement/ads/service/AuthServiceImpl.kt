package com.advertisement.ads.service

import com.advertisement.ads.configuration.AuthServiceProperties
import com.advertisement.ads.dto.UserInfo
import com.advertisement.ads.exception.ApiInternalServerErrorException
import com.advertisement.ads.exception.ApiServiceUnavailableException
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.net.URI
import java.time.Duration
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class AuthServiceImpl(
    val webClient: WebClient,
    val authServiceProperties: AuthServiceProperties
) : AuthService {

    override fun verifyAndGetUser(jwt: String): Mono<UserInfo> {
        val auth: String = authServiceProperties.username + ":" + authServiceProperties.password
        val base64Creds = Base64.getEncoder().encodeToString(auth.toByteArray())
        return webClient.post()
            .uri(URI.create("${authServiceProperties.baseUrl}/${authServiceProperties.verifyTokenPath}"))
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Basic $base64Creds")
            .body(BodyInserters.fromValue(mapOf("token" to jwt)))
            .retrieve()
            .onStatus(
                { status -> status.is5xxServerError },
                { response ->
                    logger.error("Server error: ${response.statusCode()}")
                    Mono.error(ApiInternalServerErrorException("Server error: ${response.statusCode()}"))
                }
            )
            .bodyToMono(UserInfo::class.java)
            .onErrorResume { e ->
                logger.error("Server error: ${e.message}")
                Mono.error(ApiServiceUnavailableException("Auth service is unavailable: ${e.message}"))
            }
            .retryWhen(
                Retry.backoff(authServiceProperties.retry.maxAttempts.toLong(), Duration.ofSeconds(authServiceProperties.retry.minBackoffSeconds.toLong()))
                .filter {throwable -> throwable is ApiInternalServerErrorException}
                .onRetryExhaustedThrow { retryBackoffSpec, retrySignal -> ApiServiceUnavailableException("Auth service is unavailable") })
    }
}