package com.advertisement.auth.service

import com.advertisement.auth.model.RefreshToken
import com.advertisement.auth.model.jooq.tables.references.REFRESH_TOKEN
import com.advertisement.auth.repository.RefreshTokenRepository
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class RefreshTokenServiceImpl(
    val dsl: DSLContext,
    val refreshTokenRepository: RefreshTokenRepository
) : RefreshTokenService {

    override fun save(refreshToken: RefreshToken): Mono<RefreshToken> {
        return refreshTokenRepository.save(refreshToken)
    }

    override fun revokeIfExistsByUserId(userId: UUID): Mono<Long> {
        return refreshTokenRepository.revokeIfExistsByUserId(userId)
    }

    override fun findAll(): Flux<RefreshToken> {
        return Flux.from(
            dsl
                .select(REFRESH_TOKEN.ID, REFRESH_TOKEN.USER_ID, REFRESH_TOKEN.REFRESH_TOKEN_HASH, REFRESH_TOKEN.REVOKED, REFRESH_TOKEN.CREATED_AT)
                .from(REFRESH_TOKEN)
        )
            .map { r -> RefreshToken(r.value1()!!, r.value2()!!, r.value3()!!, r.value4()!!, r.value5()!!) }
    }

    override fun findLastNotRevokedByUserId(userId: UUID): Mono<RefreshToken> {
        return Mono.from(
            dsl
                .select(REFRESH_TOKEN.ID, REFRESH_TOKEN.USER_ID, REFRESH_TOKEN.REFRESH_TOKEN_HASH, REFRESH_TOKEN.REVOKED, REFRESH_TOKEN.CREATED_AT)
                .from(REFRESH_TOKEN)
                .where(REFRESH_TOKEN.USER_ID.eq(userId)).and(REFRESH_TOKEN.REVOKED.eq(false))
                .orderBy(REFRESH_TOKEN.CREATED_AT.desc())
                .limit(1)
        )
            .map { r -> RefreshToken(r.value1()!!, r.value2()!!, r.value3()!!, r.value4()!!, r.value5()!!) }
    }
}