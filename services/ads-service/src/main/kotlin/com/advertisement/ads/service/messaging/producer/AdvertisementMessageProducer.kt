package com.advertisement.ads.service.messaging.producer

interface AdvertisementMessageProducer {
    fun incrementViewsMessage(advertisementId: String)
}