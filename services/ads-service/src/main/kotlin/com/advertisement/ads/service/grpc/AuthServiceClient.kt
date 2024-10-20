package com.advertisement.ads.service.grpc

import com.advertisement.ads.configuration.AuthServiceProperties
import com.advertisement.ads.configuration.grpc.ChannelBuilder
import com.advertisement.ads.dto.UserInfo
import com.advertisement.ads.exception.ApiInternalServerErrorException
import com.advertisement.ads.exception.ApiServiceUnavailableException
import com.advertisement.grpc.ReactorInternalAuthServiceGrpc
import com.advertisement.grpc.VerificationRequest
import mu.KotlinLogging
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class AuthServiceClient(
    val channelBuilder: ChannelBuilder,
    val authServiceProperties: AuthServiceProperties
)  {

    fun verifyAndGetUser(jwt: String): Mono<UserInfo> {
        return ReactorInternalAuthServiceGrpc.newReactorStub(channelBuilder.forTarget(authServiceProperties.baseUrl))
            .verify(VerificationRequest.newBuilder().setToken(jwt).build())
            .onErrorResume { e ->
                logger.error("Server error: ${e.message}")
                Mono.error(ApiServiceUnavailableException("Auth service is unavailable: ${e.message}"))
            }
            .retryWhen(
                Retry.backoff(
                    authServiceProperties.retry.maxAttempts.toLong(),
                    Duration.ofSeconds(authServiceProperties.retry.minBackoffSeconds.toLong())
                )
                .filter {throwable -> throwable is ApiInternalServerErrorException }
                .onRetryExhaustedThrow { retryBackoffSpec, retrySignal -> ApiServiceUnavailableException("Auth service is unavailable") })
            .map { u -> UserInfo(UUID.fromString(u.id), u.phone, u.firstName, u.lastName, u.rolesList) }
    }
}