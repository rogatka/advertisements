package com.advertisement.payment.service

import com.advertisement.payment.configuration.PayServiceProperties
import com.advertisement.payment.dto.response.PayServiceResponse
import com.advertisement.payment.exception.ApiInternalServerErrorException
import com.advertisement.payment.exception.ApiServiceUnavailableException
import com.advertisement.payment.model.Payment
import com.advertisement.payment.repository.PaymentRepository
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
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
class PaymentServiceImpl(
    val paymentRepository: PaymentRepository,
    val clock: Clock,
    val webClient: WebClient,
    val payServiceProperties: PayServiceProperties
) : PaymentService {

    override fun transfer(businessId: String, accountFrom: String, accountTo: String, amount: Long): Mono<Payment> {
        logger.info { "Going to withdraw $amount from account=$accountFrom to account=$accountTo" }
        val now = Instant.now(clock)
        return paymentRepository.findByBusinessIdAndAccountFromAndAmount(businessId, accountFrom, amount)
            .switchIfEmpty(
                transfer(amount, accountFrom, accountTo)
                .flatMap { response -> paymentRepository.save(
                    Payment(
                        UUID.randomUUID(),
                        businessId,
                        //TODO
                        response.transactionId,
//                        UUID.randomUUID().toString(),
                        accountFrom,
                        accountTo,
                        amount,
                        now
                    )) }
            )
    }

    override fun reverseTransfer(businessId: String, accountId: String, amount: Long): Mono<Payment> {
        val now = Instant.now(clock)
        return paymentRepository.findByBusinessIdAndAccountFromAndAmount(businessId, accountId, amount)
            // TODO check case when payment not found by businessId
            .flatMap { payment ->
                transfer(payment.amount, payment.accountTo, payment.accountFrom)
                    .flatMap { response -> paymentRepository.save(
                        Payment(
                            UUID.randomUUID(),
                            businessId,
                            //TODO
                            response.transactionId,
//                            UUID.randomUUID().toString(),
                            payment.accountTo,
                            payment.accountFrom,
                            payment.amount,
                            now
                        )) }
            }
    }

    private fun transfer(amount: Long, accountFrom: String, accountTo: String): Mono<PayServiceResponse> {
        logger.info { "Going to withdraw $amount from account=$accountFrom to account=$accountTo" }
        val auth: String = payServiceProperties.username + ":" + payServiceProperties.password
        val base64Creds = Base64.getEncoder().encodeToString(auth.toByteArray())
        return webClient.post()
            .uri(URI.create("${payServiceProperties.baseUrl}/${payServiceProperties.payPath}"))
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Basic $base64Creds")
            .body(BodyInserters.fromValue(mapOf(
                "fromAccountId" to accountFrom,
                "toAccountId" to accountTo,
                "amount" to amount,
            )))
            .retrieve()
            .onStatus(
                { status -> status.is5xxServerError },
                { response ->
                    logger.error("Server error: ${response.statusCode()}")
                    Mono.error(ApiInternalServerErrorException("Server error: ${response.statusCode()}"))
                }
            )
            .bodyToMono(PayServiceResponse::class.java)
            .onErrorResume { e ->
                logger.error("Server error: ${e.message}")
                Mono.error(ApiServiceUnavailableException("Pay service is unavailable: ${e.message}"))
            }
            .retryWhen(
                Retry.backoff(payServiceProperties.retry.maxAttempts.toLong(), Duration.ofSeconds(payServiceProperties.retry.minBackoffSeconds.toLong()))
                    .filter {throwable -> throwable is ApiInternalServerErrorException }
                    .onRetryExhaustedThrow { retryBackoffSpec, retrySignal -> ApiServiceUnavailableException("Pay service is unavailable") })
    }
}