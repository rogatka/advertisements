package com.advertisement.auth.service

import com.advertisement.auth.configuration.SmsServiceProperties
import com.advertisement.auth.dto.response.SmsServiceSendSmsResponse
import com.advertisement.auth.dto.response.SmsServiceTokenResponse
import com.advertisement.auth.exception.ApiInternalServerErrorException
import com.advertisement.auth.exception.ApiServiceUnavailableException
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.net.URI
import java.time.Duration
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class SmsServiceFakeImpl(
    val webClient: WebClient,
    val smsServiceProperties: SmsServiceProperties
) : SmsService {

    //TODO retry?
    override fun sendSms(phone: String, message: String): Mono<SmsServiceSendSmsResponse> {
        val map: MultiValueMap<String, String> = LinkedMultiValueMap()
        map.add("grant_type", "grant-type-mock")
        map.add("resource", "resource-mock")

        val auth: String = smsServiceProperties.username + ":" + smsServiceProperties.password
        val base64Creds = Base64.getEncoder().encodeToString(auth.toByteArray())

        return webClient.post().uri(URI.create("${smsServiceProperties.baseUrl}/${smsServiceProperties.getTokenPath}"))
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(map))
            .header(HttpHeaders.AUTHORIZATION, "Basic $base64Creds")
            .retrieve()
            .onStatus(
                { status -> status.is5xxServerError },
                { response ->
                    logger.error("Server error: ${response.statusCode()}")
                    Mono.error(ApiInternalServerErrorException("Server error: ${response.statusCode()}"))
                }
            )
            .bodyToMono(SmsServiceTokenResponse::class.java)
            .onErrorResume { e ->
                logger.error("Server error: ${e.message}")
                Mono.error(ApiServiceUnavailableException("SMS service is unavailable: ${e.message}"))
            }
            .retryWhen(Retry.backoff(smsServiceProperties.retry.maxAttempts.toLong(), Duration.ofSeconds(smsServiceProperties.retry.minBackoffSeconds.toLong()))
                .filter {throwable -> throwable is ApiInternalServerErrorException}
                .onRetryExhaustedThrow { retryBackoffSpec, retrySignal -> ApiServiceUnavailableException("SMS service is unavailable") })
            .flatMap { smsServiceTokenResponse ->
                webClient.post().uri(URI.create("${smsServiceProperties.baseUrl}/${smsServiceProperties.sendSmsPath}"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer ${smsServiceTokenResponse.accessToken}")
                    .body(BodyInserters.fromValue(mapOf("phone" to phone, "message" to message)))
                    .retrieve()
                    .onStatus(
                        { status -> status.is5xxServerError },
                        { response ->
                            logger.error("Server error: ${response.statusCode()}")
                            Mono.error(ApiInternalServerErrorException("Server error: ${response.statusCode()}"))
                        }
                    )
                    .bodyToMono(SmsServiceSendSmsResponse::class.java)
                    .retryWhen(Retry.backoff(smsServiceProperties.retry.maxAttempts.toLong(), Duration.ofSeconds(smsServiceProperties.retry.minBackoffSeconds.toLong()))
                        .filter {throwable -> throwable is ApiInternalServerErrorException}
                        .onRetryExhaustedThrow { retryBackoffSpec, retrySignal -> ApiServiceUnavailableException("SMS Service is unavailable") })
            }
    }
}