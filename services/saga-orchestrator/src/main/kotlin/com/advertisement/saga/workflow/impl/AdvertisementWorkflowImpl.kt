package com.advertisement.saga.workflow.impl

import com.advertisement.saga.workflow.AdvertisementWorkflow
import io.temporal.activity.ActivityOptions
import io.temporal.common.RetryOptions
import io.temporal.failure.ActivityFailure
import io.temporal.workflow.ActivityStub
import io.temporal.workflow.Saga
import io.temporal.workflow.Workflow
import java.time.Duration


class AdvertisementWorkflowImpl : AdvertisementWorkflow {

    private val paymentActivityOptions: ActivityOptions = ActivityOptions.newBuilder()
        .setStartToCloseTimeout(Duration.ofMinutes(1))
        .setTaskQueue("advertisement_payments")
        .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(3).build())
        .build()

    private val advertisementPriorityActivityOptions: ActivityOptions = ActivityOptions.newBuilder()
        .setStartToCloseTimeout(Duration.ofMinutes(1))
        .setTaskQueue("advertisement_priority")
        .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(3).build())
        .build()

    private val paymentActivitiesUntyped: ActivityStub = Workflow.newUntypedActivityStub(paymentActivityOptions)
    private val advertisementPriorityActivitiesUntyped: ActivityStub = Workflow.newUntypedActivityStub(advertisementPriorityActivityOptions)

    override fun increaseAdvertisementPriority(advertisementId: String, userId: String, amount:Long) {
        val sagaOptions: Saga.Options = Saga.Options.Builder().setParallelCompensation(true).build()
        val saga = Saga(sagaOptions)
        try {
            paymentActivitiesUntyped.execute("payment", Void::class.java, advertisementId, userId, amount)
            saga.addCompensation({paymentActivitiesUntyped.execute("reversePayment", Void::class.java, advertisementId, userId, amount)})

            advertisementPriorityActivitiesUntyped.execute("increasePriority", Void::class.java, advertisementId)
            saga.addCompensation({advertisementPriorityActivitiesUntyped.execute("decreasePriority", Void::class.java, advertisementId)}, advertisementId)
        } catch (cause: ActivityFailure) {
            saga.compensate()
            throw cause
        }
    }
}
