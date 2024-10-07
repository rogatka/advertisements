package com.advertisement.auth.service

import com.advertisement.auth.model.TokenType
import com.advertisement.auth.model.User
import com.auth0.jwt.interfaces.DecodedJWT
import reactor.core.publisher.Mono


interface TokenService {
    fun generateToken(user: User, tokenType: TokenType): Mono<String>
    fun verifyToken(token: String, tokenType: TokenType): Mono<DecodedJWT>
}