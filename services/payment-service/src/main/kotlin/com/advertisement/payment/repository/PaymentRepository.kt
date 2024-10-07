package com.advertisement.payment.repository

import com.advertisement.payment.model.Payment
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface PaymentRepository: ReactiveCrudRepository<Payment, UUID>, PaymentRepositoryCustom<Payment> {
    fun findByBusinessIdAndAccountFromAndAmount(businessId: String, accountFrom: String, amount: Long): Mono<Payment>
}