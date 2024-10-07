package com.advertisement.payment.repository

import reactor.core.publisher.Mono

// workaround with custom interface with 'save' method
// to avoid error 'Failed to update table [payment.payments]; Row with Id [5e53b366-3599-4a77-9aba-57cfaef928d4] does not exist'
interface PaymentRepositoryCustom<T> {
    fun <S : T> save(entity: S): Mono<S>
}
