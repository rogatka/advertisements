package com.advertisement.ads.service

import com.advertisement.ads.dto.UserInfo
import com.advertisement.ads.exception.ApiInternalServerErrorException
import com.advertisement.ads.exception.ApiNotFoundException
import com.advertisement.ads.model.Advertisement
import com.advertisement.ads.model.AdvertisementAction
import com.advertisement.ads.repository.AdvertisementRepository
import com.advertisement.ads.service.lifecycle.LifecycleProcessorAggregator
import com.advertisement.ads.service.lifecycle.LifecycleProcessorCase
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AdvertisementLifecycleServiceImpl(
    val advertisementRepository: AdvertisementRepository,
    val lifecycleProcessorAggregator: LifecycleProcessorAggregator
) : AdvertisementLifecycleService {

    override fun executeTransition(
        advertisement: Advertisement,
        event: AdvertisementAction
    ): Mono<Advertisement> {
        return ReactiveSecurityContextHolder.getContext()
            .map { c -> c.authentication }
            .map { a -> a.principal as UserInfo }
            .switchIfEmpty(Mono.error(ApiInternalServerErrorException("User not found in context")))
            .flatMap { userInfo ->
                lifecycleProcessorAggregator.forCase(LifecycleProcessorCase(advertisement.status, event))
                .process(advertisement, mapOf(
                    "userId" to userInfo.id,
                    "userRoles" to userInfo.roles
                ))
            }
    }
}