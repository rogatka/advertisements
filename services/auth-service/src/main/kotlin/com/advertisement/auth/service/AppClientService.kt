package com.advertisement.auth.service

import com.advertisement.auth.dto.response.AppClientResponse
import com.advertisement.auth.model.AppClient
import reactor.core.publisher.Mono

interface AppClientService {
    fun save(t: String): Mono<AppClientResponse>
    fun findByName(appName: String): Mono<AppClient>
}