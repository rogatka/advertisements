package com.advertisement.ads.security

import com.advertisement.ads.dto.UserInfo
import com.advertisement.ads.exception.ApiUnauthorizedException
import com.advertisement.ads.service.AuthService
import mu.KotlinLogging
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@Component
class AuthenticationManager(
    val authService: AuthService,
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.just(authentication)
            .flatMap { auth ->
                if (auth.principal == null && auth.credentials != null) {
                    Mono.just(auth)
                        .switchIfEmpty(Mono.error(ApiUnauthorizedException("Unauthorized")))
                        .map { auth.credentials.toString() }
                        .flatMap { token -> authService.verifyAndGetUser(token) }
                        .onErrorResume { e ->
                            logger.error("Error: ${e.message}")
                            Mono.error(ApiUnauthorizedException("${e.message}"))
                        }
                        .flatMap { user -> getAuthentication(user) }
                } else {
                    Mono.empty()
                }
            }
    }

    private fun getAuthentication(user: UserInfo): Mono<Authentication?> {
        return Mono.just(
            UsernamePasswordAuthenticationToken(
                user, null,
                user.roles.map { role -> SimpleGrantedAuthority("ROLE_$role") }
            )
        )
    }
}