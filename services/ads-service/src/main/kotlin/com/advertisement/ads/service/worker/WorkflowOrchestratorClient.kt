package com.advertisement.ads.service.worker

import io.temporal.client.WorkflowClient
import io.temporal.serviceclient.WorkflowServiceStubs
import io.temporal.serviceclient.WorkflowServiceStubsOptions
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class WorkflowOrchestratorClient(
    @Value("\${target}") val target: String
) {
    fun workflowClient(): WorkflowClient {
            val workflowServiceStubsOptions =
                WorkflowServiceStubsOptions.newBuilder()
                    .setTarget(target)
                    .build()
            val workflowServiceStubs = WorkflowServiceStubs.newServiceStubs(workflowServiceStubsOptions)
            return WorkflowClient.newInstance(workflowServiceStubs)
        }
}
