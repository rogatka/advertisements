package com.advertisement.payment.repository

import com.advertisement.payment.model.UserAccount
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface UserAccountRepository: ReactiveCrudRepository<UserAccount, UUID> {
    fun findByUserId(userId: String): Mono<UserAccount>
}