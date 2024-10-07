package com.advertisement.payment.service

import com.advertisement.payment.model.Payment
import reactor.core.publisher.Mono

interface PaymentService {
    fun transfer(businessId: String, accountFrom: String, accountTo: String, amount: Long): Mono<Payment>
    fun reverseTransfer(businessId: String, accountId: String, amount: Long): Mono<Payment>
}