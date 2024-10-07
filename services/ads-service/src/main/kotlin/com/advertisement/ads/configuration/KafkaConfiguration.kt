package com.advertisement.ads.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin.NewTopics


@Configuration
class KafkaConfiguration(
    @Value("\${kafka.topics.advertisement-views.name}") val viewsTopicName: String,
    @Value("\${kafka.topics.advertisement-views.partitions}") val viewsTopicPartitions: Int,
) {

    @Bean
    fun topics(): NewTopics {
        return NewTopics(
            TopicBuilder.name(viewsTopicName)
                .partitions(viewsTopicPartitions)
                .build()
        )
    }
}