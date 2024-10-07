package com.advertisement.ads.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
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
            "/api/v1/advertisements",
            "/api/v1/advertisements/**",
            "/api/v1/files/**",
            "/v3/api-docs",
            "/actuator/**"
        )
    }

    @Bean
    fun springWebFilterChain(serverHttpSecurity: ServerHttpSecurity): SecurityWebFilterChain {
        return serverHttpSecurity
            .authenticationManager(authenticationManager)
            .securityContextRepository(securityContextRepository)
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .authorizeExchange { exchanges -> exchanges
                .pathMatchers(HttpMethod.GET, *PERMITTED_URL)
                .permitAll()
                .pathMatchers(HttpMethod.POST, "api/v1/advertisements/*:viewed")
                .permitAll()
                .pathMatchers("/api/v1/cities").hasRole("USER")
                .pathMatchers("/api/v1/advertisements/my").hasRole("USER")
                .anyExchange()
                .authenticated()
            }
            .build()
    }
}