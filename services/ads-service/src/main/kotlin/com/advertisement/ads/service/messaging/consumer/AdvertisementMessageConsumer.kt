package com.advertisement.ads.service.messaging.consumer

import com.advertisement.ads.exception.ApiNotFoundException
import com.advertisement.ads.repository.AdvertisementRepository
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@Service
class AdvertisementMessageConsumer(
    val advertisementRepository: AdvertisementRepository,
) {

    @KafkaListener(topics = ["\${kafka.topics.advertisement-views.name}"])
    fun processMessage(record: ConsumerRecord<String, String>) {
        logger.info("Received message: $record")
        advertisementRepository.findById(record.key())
            .switchIfEmpty(Mono.error(ApiNotFoundException("Advertisement with id=${record.key()} not found")))
            .flatMap { adv ->
                val advertisementCopy = adv.copy(
                    views = adv.views + record.value().toLong()
                )
                advertisementRepository.save(advertisementCopy)
            }
            //TODO to think how to avoid blocking
            .block()
    }
}