package com.advertisement.payment.service.activities

import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface PaymentActivities {

    @ActivityMethod(name = "payment")
    fun payment(advertisementId: String, userId: String, amount:Long)

    @ActivityMethod(name = "reversePayment")
    fun reversePayment(advertisementId: String, userId: String, amount:Long)
}
