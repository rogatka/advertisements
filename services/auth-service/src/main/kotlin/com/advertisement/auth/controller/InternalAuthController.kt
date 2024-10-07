package com.advertisement.auth.controller

import com.advertisement.auth.dto.request.InternalRegistrationRequest
import com.advertisement.auth.dto.response.InternalRegistrationResponse
import com.advertisement.auth.dto.request.UserVerificationRequest
import com.advertisement.auth.model.UserWithRoles
import com.advertisement.auth.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/internal/auth")
class InternalAuthController(val authService: AuthService) {

    //TODO change to GRPC
    @PostMapping("/registration")
    fun register(@Valid @RequestBody internalRegistrationRequest: InternalRegistrationRequest,
                 @Valid @RequestHeader(name = "X-Internal-Auth") internalAuthHeader: String)
    : Mono<ResponseEntity<InternalRegistrationResponse>> {
        return authService.register(internalRegistrationRequest, internalAuthHeader)
            .map { r -> ResponseEntity.ok(r) }
    }

    @PostMapping("/verification")
    fun verify(@Valid @RequestBody userVerificationRequest: UserVerificationRequest)
    : Mono<ResponseEntity<UserWithRoles>> {
        return authService.verify(userVerificationRequest.token)
            .map { r -> ResponseEntity.ok(r) }
    }
}