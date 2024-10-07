package com.advertisement.auth.service

import com.advertisement.auth.model.RefreshToken
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface RefreshTokenService {
    fun save(refreshToken: RefreshToken): Mono<RefreshToken>
    fun revokeIfExistsByUserId(userId: UUID): Mono<Long>
    fun findAll(): Flux<RefreshToken>
    fun findLastNotRevokedByUserId(userId: UUID): Mono<RefreshToken>
}