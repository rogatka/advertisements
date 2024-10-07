package com.advertisement.payment.service.activities.impl

import com.advertisement.payment.exception.ApiNotFoundException
import com.advertisement.payment.repository.UserAccountRepository
import com.advertisement.payment.service.PaymentService
import com.advertisement.payment.service.activities.PaymentActivities
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@Component
class PaymentActivitiesImpl(
    val userAccountRepository: UserAccountRepository,
    val paymentService: PaymentService,
    @Value("\${advertisement-platform-account-id}") val advertisementPlatformAccountId: String
) : PaymentActivities {

    override fun payment(advertisementId: String, userId: String, amount:Long) {
        userAccountRepository.findByUserId(userId)
            .switchIfEmpty(Mono.error(ApiNotFoundException("Account for userId=${userId} not found")))
            .flatMap { userAccount -> paymentService.transfer(
                advertisementId,
                userAccount.accountId,
                advertisementPlatformAccountId,
                amount
            ) }
            .block()
    }

    override fun reversePayment(advertisementId: String, userId: String, amount:Long) {
        userAccountRepository.findByUserId(userId)
            .switchIfEmpty(Mono.error(ApiNotFoundException("Account for userId=${userId} not found")))
            .flatMap { userAccount -> paymentService.reverseTransfer(
                advertisementId,
                userAccount.accountId,
                amount
            ) }
            .block()
    }
}
