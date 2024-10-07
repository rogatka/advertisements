package com.advertisement.auth.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.*
import org.springframework.security.web.server.SecurityWebFilterChain


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration(
    val authenticationManager: AuthenticationManager,
    val securityContextRepository: SecurityContextRepository
) {

    companion object {
        val PERMITTED_URL: Array<String> = arrayOf(
            "/api/v1/otp",
            "/api/v1/auth/access-token",
            "/api/v1/internal/auth/registration",
            "/api/v1/auth/login",
            "/v3/api-docs",
            "/actuator/**"
        )
    }

    @Bean
    fun springWebFilterChain(serverHttpSecurity: ServerHttpSecurity): SecurityWebFilterChain {
        return serverHttpSecurity
            .authenticationManager(authenticationManager)
            .securityContextRepository(securityContextRepository)
            .csrf(CsrfSpec::disable)
            .formLogin(FormLoginSpec::disable)
            .httpBasic(HttpBasicSpec::disable)
            .authorizeExchange { exchanges -> exchanges
                .pathMatchers(*PERMITTED_URL)
                .permitAll()
                .pathMatchers("/api/v1/users").hasRole("ADMINISTRATOR")
                .anyExchange()
                .authenticated()
            }
            .build()
    }
}