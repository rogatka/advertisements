package com.advertisement.ads.service.worker

import com.advertisement.ads.service.activities.AdvertisementPriorityActivities
import io.temporal.worker.WorkerFactory
import io.temporal.worker.WorkerOptions
import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class AdvertisementWorker(
    val workflowOrchestratorClient: WorkflowOrchestratorClient,
    val advertisementPriorityActivities: AdvertisementPriorityActivities
) {

    @PostConstruct
    fun createWorker() {
        val workflowClient = workflowOrchestratorClient.workflowClient()
        val workerOptions = WorkerOptions.newBuilder().build()
        val workerFactory = WorkerFactory.newInstance(workflowClient)
        val worker = workerFactory.newWorker("advertisement_priority", workerOptions)
        worker.registerActivitiesImplementations(advertisementPriorityActivities)
        workerFactory.start()
    }
}