package com.advertisement.auth.controller

import com.advertisement.auth.service.AuthService
import com.advertisement.grpc.ReactorInternalAuthServiceGrpc
import com.advertisement.grpc.VerificationRequest
import org.lognet.springboot.grpc.GRpcService
import reactor.core.publisher.Mono

@GRpcService
class InternalAuthController(val authService: AuthService): ReactorInternalAuthServiceGrpc.InternalAuthServiceImplBase() {

    override fun register(request: com.advertisement.grpc.InternalRegistrationRequest?): Mono<com.advertisement.grpc.InternalRegistrationResponse> {
        return authService.register(request!!, request.authHeader)
    }

    override fun verify(request: VerificationRequest?): Mono<com.advertisement.grpc.UserWithRoles> {
        return authService.verify(request!!.token)
    }
}