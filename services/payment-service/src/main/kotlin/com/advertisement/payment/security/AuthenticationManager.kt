package com.advertisement.payment.security

import com.advertisement.payment.configuration.SecurityProperties
import com.advertisement.payment.exception.ApiException
import com.advertisement.payment.exception.ApiUnauthorizedException
import mu.KotlinLogging
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@Component
class AuthenticationManager(
    val securityProperties: SecurityProperties
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.just(authentication)
            .flatMap { auth ->
                if (auth.principal != null && auth.credentials != null) {
                Mono.just(auth)
                    .switchIfEmpty(Mono.error(ApiUnauthorizedException("Unauthorized")))
                    .filter {auth ->
                        val username = auth.principal.toString()
                        val password = auth.credentials.toString()
                        username == securityProperties.internalAuth.username && password == securityProperties.internalAuth.password
                    }
                    .switchIfEmpty(Mono.error(ApiException("Invalid credentials")))
                } else {
                    Mono.empty()
                }
            }
    }
}