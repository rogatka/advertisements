package com.advertisement.saga.security

import com.advertisement.saga.configuration.SecurityProperties
import com.advertisement.saga.exception.ApiException
import mu.KotlinLogging
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
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
                    .filter {a ->
                        val username = a.principal.toString()
                        val password = a.credentials.toString()
                        return@filter username == securityProperties.internalAuth.username && password == securityProperties.internalAuth.password
                    }
                    .switchIfEmpty(Mono.error(ApiException("Invalid credentials")))
                    .flatMap { a -> Mono.just(UsernamePasswordAuthenticationToken(a.principal, a.credentials, listOf())) }
                } else {
                    Mono.empty()
                }
            }
    }
}