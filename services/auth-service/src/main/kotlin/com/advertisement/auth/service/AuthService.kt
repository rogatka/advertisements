package com.advertisement.auth.service

import com.advertisement.auth.dto.request.LoginRequest
import com.advertisement.auth.dto.response.AccessTokenResponse
import com.advertisement.auth.dto.response.LoginResponse
import com.advertisement.grpc.InternalRegistrationRequest
import com.advertisement.grpc.InternalRegistrationResponse
import com.advertisement.grpc.UserWithRoles
import reactor.core.publisher.Mono

interface AuthService {
    fun login(loginRequest: LoginRequest): Mono<LoginResponse>
    fun logout(): Mono<Unit>
    fun getAccessToken(refreshToken: String): Mono<AccessTokenResponse>
    fun register(internalRegistrationRequest: InternalRegistrationRequest, internalAuthHeader: String): Mono<InternalRegistrationResponse>
    fun verify(token: String): Mono<UserWithRoles>
}