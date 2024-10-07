package com.advertisement.saga.configuration.orchestration

import io.temporal.client.WorkflowClient
import io.temporal.serviceclient.WorkflowServiceStubs
import io.temporal.serviceclient.WorkflowServiceStubsOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class WorkflowOrchestratorClient(
    @Value("\${target}") val target:String
) {

    fun workflowClient(): WorkflowClient {
        val workflowServiceStubsOptions: WorkflowServiceStubsOptions =
            WorkflowServiceStubsOptions.newBuilder()
                .setTarget(target)
                .build()
        val workflowServiceStubs: WorkflowServiceStubs =
            WorkflowServiceStubs.newServiceStubs(workflowServiceStubsOptions)
        return WorkflowClient.newInstance(workflowServiceStubs)
    }
}
