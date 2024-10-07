package com.advertisement.auth.service

import com.advertisement.auth.dto.response.AppClientResponse
import com.advertisement.auth.model.AppClient
import com.advertisement.auth.model.jooq.tables.references.APP_CLIENTS
import com.advertisement.auth.repository.AppClientRepository
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AppClientServiceImpl(
    val appClientRepository: AppClientRepository,
    val dsl: DSLContext
) : AppClientService {

    override fun save(t: String): Mono<AppClientResponse> {
        return appClientRepository.save(t)
    }

    override fun findByName(appName: String): Mono<AppClient> {
        return Mono.from(dsl
            .select(APP_CLIENTS.ID, APP_CLIENTS.NAME, APP_CLIENTS.PASSWORD, APP_CLIENTS.DISABLED, APP_CLIENTS.CREATED_AT, APP_CLIENTS.DISABLED_AT)
            .from(APP_CLIENTS)
            .where(APP_CLIENTS.NAME.equalIgnoreCase(appName)))
            .map { r -> AppClient(r.value1()!!, r.value2()!!, r.value3()!!, r.value4()!!, r.value5()!!, r.value6()) }
    }
}