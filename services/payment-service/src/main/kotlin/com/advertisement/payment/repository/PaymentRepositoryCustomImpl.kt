package com.advertisement.payment.repository

import com.advertisement.payment.model.Payment
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Transactional
@Component
class PaymentRepositoryCustomImpl(
    val entityTemplate: R2dbcEntityTemplate
) : PaymentRepositoryCustom<Payment> {

    override fun <S : Payment> save(entity: S): Mono<S> {
        return entityTemplate.insert(entity)
//            .using(entity)
//        return databaseClient.sql("insert into payment.payments(id, business_id, transaction_id, account_from, account_to, amount, created_at) " +
//                "values (:id, :business_id, :transaction_id, :account_from, :account_to, :amount, :created_at)")
//            .bind("id", entity.id)
//            .bind("business_id", entity.businessId)
//            .bind("transaction_id", entity.transactionId)
//            .bind("account_from", entity.accountFrom)
//            .bind("account_to", entity.accountTo)
//            .bind("amount", entity.amount)
//            .bind("created_at", entity.createdAt)
//            .fetch()
//            .one()
    }


}