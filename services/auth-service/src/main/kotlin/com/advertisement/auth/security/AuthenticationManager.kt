package com.advertisement.auth.security

import com.advertisement.auth.configuration.SecurityProperties
import com.advertisement.auth.exception.ApiException
import com.advertisement.auth.exception.ApiUnauthorizedException
import com.advertisement.auth.model.AppClient
import com.advertisement.auth.model.TokenType
import com.advertisement.auth.model.UserWithRoles
import com.advertisement.auth.service.AppClientService
import com.advertisement.auth.service.TokenService
import com.advertisement.auth.service.UserService
import com.advertisement.auth.utils.Utils
import mu.KotlinLogging
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

private val logger = KotlinLogging.logger {}

@Component
class AuthenticationManager(
    val tokenService: TokenService,
    val userService: UserService,
    val appClientService: AppClientService,
    val securityProperties: SecurityProperties
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.just(authentication)
            .flatMap { auth ->
                if (auth.principal != null && auth.credentials != null) {
                Mono.just(auth)
                    .switchIfEmpty(Mono.error(ApiUnauthorizedException("Unauthorized")))
                    .flatMap { a ->
                        val username = a.principal.toString()
                        val password = a.credentials.toString()
                        appClientService.findByName(username)
                            .switchIfEmpty(Mono.error(ApiException("Client not found")))
                            .filter{client -> client.password.contentEquals(Utils.calculateHash(password, securityProperties.internalAuth.hashingSalt.toByteArray())) }
                            .switchIfEmpty(Mono.error(ApiException("Invalid credentials")))
                    }
                    .flatMap { client -> getAuthentication(client) }
            } else if (auth.principal == null && auth.credentials != null) {
                    Mono.just(auth)
                        .switchIfEmpty(Mono.error(ApiUnauthorizedException("Unauthorized")))
                        .map { auth.credentials.toString() }
                        .flatMap { token -> tokenService.verifyToken(token, TokenType.ACCESS) }
                        .onErrorResume { e ->
                            logger.error("Error: ${e.message}")
                            Mono.error(ApiUnauthorizedException("${e.message}"))
                        }
                        .flatMap { decodedJWT -> userService.findWithRolesById(decodedJWT.claims["userId"]!!.`as`(UUID::class.java)) }
                        .switchIfEmpty(Mono.error(ApiException("User not found")))
                        .flatMap { user -> getAuthentication(user) }
            } else {
                Mono.empty()
            }
            }
    }

    private fun getAuthentication(user: UserWithRoles): Mono<Authentication?> {
        return Mono.just(
            UsernamePasswordAuthenticationToken(
                user, null,
                user.roles.map { role -> SimpleGrantedAuthority("ROLE_$role") }
            )
        )
    }

    private fun getAuthentication(appClient: AppClient): Mono<Authentication?> {
        return Mono.just(UsernamePasswordAuthenticationToken(appClient, null,null))
    }
}