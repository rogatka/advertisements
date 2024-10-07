package com.advertisement.auth.security

import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.util.Base64


@Component
class SecurityContextRepository (
    val authenticationManager: AuthenticationManager
): ServerSecurityContextRepository {

    companion object {
        private const val BEARER_AUTH_PREFIX = "Bearer "
        private const val BASIC_AUTH_PREFIX = "Basic "
    }

    override fun save(exchange: ServerWebExchange?, context: SecurityContext?): Mono<Void> {
        //TODO
        throw UnsupportedOperationException("Not supported")
    }

    override fun load(serverWebExchange: ServerWebExchange): Mono<SecurityContext> {
        return Mono.just(serverWebExchange.request)
            .mapNotNull { serverHttpRequest -> serverHttpRequest.headers.getFirst(HttpHeaders.AUTHORIZATION) }
            .flatMap { header ->
                // flow for users (jwt based)
                if (header != null && header.startsWith(BEARER_AUTH_PREFIX)) {
                    val bearerToken = header.replace(BEARER_AUTH_PREFIX, "")
                    authenticationManager.authenticate(UsernamePasswordAuthenticationToken(null, bearerToken))
                // flow for client-applications (basic creds)
                } else if (header != null && header.startsWith(BASIC_AUTH_PREFIX)) {
                    val credsBase64 = header.replace(BASIC_AUTH_PREFIX, "")
                    val creds = Base64.getDecoder().decode(credsBase64)
                    val credsString = String(creds, StandardCharsets.UTF_8)
                    val username = credsString.split(":")[0]
                    val password = credsString.split(":")[1]
                    authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
                } else {
                    Mono.empty()
                }
             }
            .map { auth -> SecurityContextImpl(auth) }
    }
}