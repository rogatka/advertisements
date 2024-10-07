package com.advertisement.auth.controller

import com.advertisement.auth.dto.response.AccessTokenResponse
import com.advertisement.auth.dto.request.LoginRequest
import com.advertisement.auth.dto.response.LoginResponse
import com.advertisement.auth.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(val authService: AuthService) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody loginRequest: LoginRequest): Mono<ResponseEntity<LoginResponse>> {
        return authService.login(loginRequest)
            .map { r -> ResponseEntity.ok(r) }
    }

    @PostMapping("/logout")
    fun logout(): Mono<ResponseEntity<Unit>> {
        return authService.logout()
            .map { ResponseEntity.noContent().build() }
    }

    @PostMapping("/access-token")
    fun getAccessToken(@Valid @RequestHeader(name = "Refresh-Token") refreshToken: String)
    : Mono<ResponseEntity<AccessTokenResponse>> {
        return authService.getAccessToken(refreshToken)
            .map { r -> ResponseEntity.ok(r) }
    }
}