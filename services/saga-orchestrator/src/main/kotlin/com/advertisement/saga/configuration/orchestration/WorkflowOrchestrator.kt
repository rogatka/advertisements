package com.advertisement.saga.configuration.orchestration

import reactor.core.publisher.Mono

interface WorkflowOrchestrator {
    fun increaseAdvertisementPriority(advertisementId: String, userId: String, amount:Long): Mono<Unit>
}
