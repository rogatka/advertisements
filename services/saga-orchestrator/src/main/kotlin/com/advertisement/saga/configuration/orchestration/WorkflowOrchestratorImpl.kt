package com.advertisement.saga.configuration.orchestration

import com.advertisement.saga.workflow.AdvertisementWorkflow
import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowOptions
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class WorkflowOrchestratorImpl(
    val workflowOrchestratorClient: WorkflowOrchestratorClient
) : WorkflowOrchestrator {

    override fun increaseAdvertisementPriority(advertisementId: String, userId: String, amount:Long): Mono<Unit> {
        val workflowClient: WorkflowClient = workflowOrchestratorClient.workflowClient()
        val advertisementWorkflow= workflowClient.newWorkflowStub(AdvertisementWorkflow::class.java,
                WorkflowOptions.newBuilder()
                    .setWorkflowId(advertisementId)
                    .setTaskQueue("advertisement_paid_priority")
                    .build()
            )
        WorkflowClient.start(advertisementWorkflow::increaseAdvertisementPriority, advertisementId, userId, amount)
        //TODO remove mono?
        return Mono.empty()
    }
}
