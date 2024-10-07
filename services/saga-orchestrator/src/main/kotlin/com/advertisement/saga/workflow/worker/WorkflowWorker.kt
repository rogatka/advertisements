package com.advertisement.saga.workflow.worker

import com.advertisement.saga.configuration.orchestration.WorkflowOrchestratorClient
import com.advertisement.saga.workflow.impl.AdvertisementWorkflowImpl
import io.temporal.worker.WorkerFactory
import io.temporal.worker.WorkerOptions
import io.temporal.worker.WorkflowImplementationOptions
import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class WorkflowWorker(
    val workflowOrchestratorClient: WorkflowOrchestratorClient
) {

    @PostConstruct
    fun createWorker() {
        val workerOptions = WorkerOptions.newBuilder().build()
        val workflowClient = workflowOrchestratorClient.workflowClient()
        val workflowImplementationOptions =
            WorkflowImplementationOptions.newBuilder()
                .setFailWorkflowExceptionTypes(NullPointerException::class.java)
                .build()
        val workerFactory = WorkerFactory.newInstance(workflowClient)
        val worker = workerFactory.newWorker("advertisement_paid_priority", workerOptions)
        worker.registerWorkflowImplementationTypes(workflowImplementationOptions, AdvertisementWorkflowImpl::class.java)
        worker.registerActivitiesImplementations()
        workerFactory.start()
    }
}