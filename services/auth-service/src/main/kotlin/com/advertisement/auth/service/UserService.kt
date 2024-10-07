package com.advertisement.auth.service

import com.advertisement.auth.model.User
import com.advertisement.auth.model.UserWithRoles
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface UserService {
    fun save(t: User): Mono<User>
    fun findAll(): Flux<User>
    fun findByPhone(phone: String): Mono<User>
    fun findById(id: UUID): Mono<User>
    fun findWithRolesById(id: UUID): Mono<UserWithRoles>
}