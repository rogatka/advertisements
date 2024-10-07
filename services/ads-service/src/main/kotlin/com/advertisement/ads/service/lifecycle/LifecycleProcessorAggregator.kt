package com.advertisement.ads.service.lifecycle

import com.advertisement.ads.exception.ApiInternalServerErrorException
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.util.function.Function
import java.util.stream.Collectors

private val logger = KotlinLogging.logger {}

@Component
class LifecycleProcessorAggregator(
    final val lifecycleProcessors: List<LifecycleProcessor>
) {
    val lifecycleProcessorsMap: Map<LifecycleProcessorCase, LifecycleProcessor> =
        lifecycleProcessors
        .stream()
        .collect(Collectors.toMap(LifecycleProcessor::getCase, Function.identity()))

    fun forCase(lifecycleProcessorCase: LifecycleProcessorCase) : LifecycleProcessor {
        return lifecycleProcessorsMap[lifecycleProcessorCase] ?: throw ApiInternalServerErrorException("No processor for case $lifecycleProcessorCase")
    }
}