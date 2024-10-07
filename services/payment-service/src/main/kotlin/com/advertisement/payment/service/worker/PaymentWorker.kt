package com.advertisement.payment.service.worker

import com.advertisement.payment.service.activities.PaymentActivities
import io.temporal.worker.WorkerFactory
import io.temporal.worker.WorkerOptions
import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class PaymentWorker(
    val workflowOrchestratorClient: WorkflowOrchestratorClient,
    val paymentActivities: PaymentActivities
) {

    @PostConstruct
    fun createWorker() {
        val workflowClient = workflowOrchestratorClient.workflowClient()
        val workerOptions = WorkerOptions.newBuilder().build()
        val workerFactory = WorkerFactory.newInstance(workflowClient)
        val worker = workerFactory.newWorker("advertisement_payments", workerOptions)
        worker.registerActivitiesImplementations(paymentActivities)
        workerFactory.start()
    }
}