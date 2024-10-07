package com.advertisement.ads.service.lifecycle

import com.advertisement.ads.model.Advertisement
import reactor.core.publisher.Mono

interface LifecycleProcessor {
    fun process(advertisement: Advertisement, context: Map<String, Any>): Mono<Advertisement>
    fun getCase(): LifecycleProcessorCase
}