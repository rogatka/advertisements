//package com.advertisement.payment.controller
//
//import com.advertisement.payment.dto.request.CreatePaymentRequest
//import com.advertisement.payment.exception.ApiNotFoundException
//import com.advertisement.payment.model.Payment
//import com.advertisement.payment.repository.UserAccountRepository
//import com.advertisement.payment.service.PaymentService
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.http.ResponseEntity
//import org.springframework.security.core.Authentication
//import org.springframework.web.bind.annotation.*
//import reactor.core.publisher.Mono
//
//@RestController
//@RequestMapping("/api/v1/payments")
//class PaymentController(
//    val userAccountRepository: UserAccountRepository,
//    val paymentService: PaymentService,
//    @Value("\${advertisement-platform-account-id}") val advertisementPlatformAccountId: String
//) {
//
//    @PostMapping
//    fun payment(@RequestBody createPaymentRequest: CreatePaymentRequest, authentication: Authentication)
//            : Mono<ResponseEntity<Payment>> {
//        return userAccountRepository.findByUserId(createPaymentRequest.userId)
//            .switchIfEmpty(Mono.error(ApiNotFoundException("Account for userId=${createPaymentRequest.userId} not found")))
//            .flatMap { userAccount -> paymentService.transfer(
//                createPaymentRequest.businessId,
//                advertisementPlatformAccountId,
//                userAccount.accountId,
//                createPaymentRequest.amount
//            ) }
//            .map { r -> ResponseEntity.ok(r) }
//    }
//
//    @PostMapping("/reverse")
//    fun reversePayment(@RequestParam businessId: String, authentication: Authentication)
//            : Mono<ResponseEntity<Payment>> {
//        return paymentService.reverseTransfer(businessId)
//            .map { r -> ResponseEntity.ok(r) }
//    }
//}
