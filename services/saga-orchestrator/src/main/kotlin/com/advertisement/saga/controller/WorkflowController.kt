package com.advertisement.saga.controller

import com.advertisement.saga.configuration.orchestration.WorkflowOrchestrator
import com.advertisement.saga.dto.request.CreatePaymentRequest
import com.advertisement.saga.workflow.AdvertisementWorkflow
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/workflows")
class WorkflowController(
    val workflowOrchestrator: WorkflowOrchestrator
) {

    @PostMapping("/advertisement-payments")
    fun pay(@RequestBody createPaymentRequest: CreatePaymentRequest, authentication: Authentication)
            : Mono<ResponseEntity<Unit>> {
        return workflowOrchestrator.increaseAdvertisementPriority(createPaymentRequest.advertisementId, createPaymentRequest.userId, createPaymentRequest.amount)
            .map { r -> ResponseEntity.ok(r) }
    }
}
