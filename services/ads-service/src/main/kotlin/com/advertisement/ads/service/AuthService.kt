package com.advertisement.ads.service

import com.advertisement.ads.dto.UserInfo
import reactor.core.publisher.Mono

interface AuthService {
    fun verifyAndGetUser(jwt: String): Mono<UserInfo>
}