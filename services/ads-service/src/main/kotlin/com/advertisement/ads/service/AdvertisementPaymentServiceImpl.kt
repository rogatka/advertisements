package com.advertisement.ads.service

import com.advertisement.ads.configuration.SagaOrchestratorProperties
import com.advertisement.ads.dto.UserInfo
import com.advertisement.ads.exception.ApiInternalServerErrorException
import com.advertisement.ads.exception.ApiServiceUnavailableException
import com.advertisement.ads.model.AdvertisementPayment
import com.advertisement.ads.model.PaymentAmount
import com.advertisement.ads.model.PaymentStatus
import com.advertisement.ads.model.PaymentStatusHistory
import com.advertisement.ads.repository.AdvertisementPaymentRepository
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.net.URI
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class AdvertisementPaymentServiceImpl(
    val advertisementPaymentRepository: AdvertisementPaymentRepository,
    val clock: Clock,
    val webClient: WebClient,
    val sagaOrchestratorProperties: SagaOrchestratorProperties
) : AdvertisementPaymentService {

    @Transactional
    override fun pay(userId: String, advertisementId: String, amount: Long): Mono<AdvertisementPayment> {
        logger.info { "Starting payment for $advertisementId on $amount rubles" }
        val now = Instant.now(clock)
        return advertisementPaymentRepository.findByAdvertisementId(advertisementId)
            .defaultIfEmpty(AdvertisementPayment(
                null,
                userId,
                advertisementId,
                PaymentAmount(amount),
                PaymentStatus.PENDING,
                now,
                null,
                statusHistory = listOf(),
                details = null
            ))
            .flatMap{a ->
                val advCopy = a.copy(
                    status = PaymentStatus.PENDING,
                    updateDate = now,
                    statusHistory = listOf(*a.statusHistory.toTypedArray(),
                        PaymentStatusHistory(PaymentStatus.PENDING, now)
                    )
                )
                advertisementPaymentRepository.save(advCopy)
            }
            .flatMap { adv ->  pay(adv)
                .flatMap { Mono.just(adv) }
            }
    }

    private fun pay(advertisementPayment: AdvertisementPayment): Mono<UserInfo> {
        val auth: String = sagaOrchestratorProperties.username + ":" + sagaOrchestratorProperties.password
        val base64Creds = Base64.getEncoder().encodeToString(auth.toByteArray())
        return webClient.post()
            .uri(URI.create("${sagaOrchestratorProperties.baseUrl}/${sagaOrchestratorProperties.payPath}"))
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Basic $base64Creds")
            .body(BodyInserters.fromValue(mapOf(
                "advertisementId" to advertisementPayment.advertisementId,
                "userId" to advertisementPayment.userId,
                "amount" to advertisementPayment.amount.amount,
            )))
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
                Mono.error(ApiServiceUnavailableException("Saga orchestrator is unavailable: ${e.message}"))
            }
            .retryWhen(
                Retry.backoff(sagaOrchestratorProperties.retry.maxAttempts.toLong(), Duration.ofSeconds(sagaOrchestratorProperties.retry.minBackoffSeconds.toLong()))
                    .filter {throwable -> throwable is ApiInternalServerErrorException }
                    .onRetryExhaustedThrow { retryBackoffSpec, retrySignal -> ApiServiceUnavailableException("Saga orchestrator is unavailable") })
    }
}