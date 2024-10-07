package com.advertisement.auth.repository

import com.advertisement.auth.dto.OtpType
import com.advertisement.auth.model.Otp
import com.advertisement.auth.model.Role
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.OffsetDateTime
import java.util.*

@Repository
class OtpRepository (
   val databaseClient: DatabaseClient
)  {

    fun save(otp: Otp):Mono<Otp> {
        return databaseClient.sql("""
            INSERT INTO advertisement.otp (id, phone, otp_hash, type, created_at) VALUES
            (:id, :phone, :otp_hash, :type, :created_at)""".trimIndent())
            .bind("id", otp.id)
            .bind("phone", otp.phone)
            .bind("otp_hash", otp.hashedCode)
            .bind("type", otp.otpType.name)
            .bind("created_at", otp.createdAt)
            .filter{statement -> statement.returnGeneratedValues("id", "phone", "otp_hash", "type", "created_at")}
            .fetch()
            .first()
            .map { r -> Otp(r["id"] as UUID, r["phone"] as String, otp.hashedCode, OtpType.valueOf(r["type"] as String), (r["created_at"] as OffsetDateTime).toInstant()) }
    }
}