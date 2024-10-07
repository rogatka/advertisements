package com.advertisement.saga.workflow

import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod

@WorkflowInterface
interface AdvertisementWorkflow {

    @WorkflowMethod
    fun increaseAdvertisementPriority(advertisementId: String, userId: String, amount:Long)
}
