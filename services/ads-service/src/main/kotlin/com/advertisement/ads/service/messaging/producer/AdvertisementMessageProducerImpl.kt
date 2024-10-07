package com.advertisement.ads.service.messaging.producer

import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class AdvertisementMessageProducerImpl(
    val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${kafka.topics.advertisement-views.name}") val viewsTopicName: String,
) : AdvertisementMessageProducer {

    override fun incrementViewsMessage(advertisementId: String) {
        //TODO rework message format later
        kafkaTemplate.send(viewsTopicName, advertisementId, "1")
    }
}