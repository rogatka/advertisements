package com.advertisement.ads.service.activities

import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface AdvertisementPriorityActivities {

    @ActivityMethod(name = "increasePriority")
    fun increasePriority(advertisementId: String)

    @ActivityMethod(name = "decreasePriority")
    fun decreasePriority(advertisementId: String)
}
