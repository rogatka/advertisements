package com.advertisement.ads.security

import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class SecurityContextRepository (
    val authenticationManager: AuthenticationManager
): ServerSecurityContextRepository {

    companion object {
        private const val BEARER_AUTH_PREFIX = "Bearer "
    }

    override fun save(exchange: ServerWebExchange?, context: SecurityContext?): Mono<Void> {
        throw UnsupportedOperationException("Not supported")
    }

    override fun load(serverWebExchange: ServerWebExchange): Mono<SecurityContext> {
        return Mono.just(serverWebExchange.request)
            .mapNotNull { serverHttpRequest -> serverHttpRequest.headers.getFirst(HttpHeaders.AUTHORIZATION) }
            .filter{ header -> header != null && header.startsWith(BEARER_AUTH_PREFIX)}
            .switchIfEmpty(Mono.empty())
            .map { header -> header!!.replace(BEARER_AUTH_PREFIX, "") }
            .flatMap { jwt -> authenticationManager.authenticate(UsernamePasswordAuthenticationToken(null, jwt)) }
            .map { auth -> SecurityContextImpl(auth) }
    }
}