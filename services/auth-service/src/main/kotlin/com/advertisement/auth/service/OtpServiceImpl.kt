package com.advertisement.auth.service

import com.advertisement.auth.configuration.SecurityProperties
import com.advertisement.auth.dto.request.OtpRequest
import com.advertisement.auth.dto.OtpType
import com.advertisement.auth.dto.response.SmsSendResponse
import com.advertisement.auth.model.Otp
import com.advertisement.auth.model.jooq.tables.Otp.Companion.OTP
import com.advertisement.auth.repository.OtpRepository
import com.advertisement.auth.utils.Utils
import org.jooq.DSLContext
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Clock
import java.time.Instant
import java.util.*


private const val SEND_SMS_ON_LOGIN_MESSAGE_CODE = "login.code"

@Service
class OtpServiceImpl(
    val messageSource: MessageSource,
    val smsService: SmsService,
    val otpRepository: OtpRepository,
    val dsl: DSLContext,
    val securityProperties: SecurityProperties,
    val clock: Clock
) : OtpService {

    @Transactional
    override fun requestOtp(otpRequest: OtpRequest, locale: Locale): Mono<SmsSendResponse> {
        val otp = Utils.generateCode()
        val message = messageSource.getMessage(SEND_SMS_ON_LOGIN_MESSAGE_CODE, arrayOf(otp), locale)
        return smsService.sendSms(
            otpRequest.phone,
            message
        )
            .flatMap { _ ->
                val hashedOtp = Utils.calculateHash(otp, securityProperties.otp.hashingSalt.toByteArray())
                otpRepository.save(
                    Otp(
                        UUID.randomUUID(),
                        otpRequest.phone,
                        hashedOtp,
                        otpRequest.otpType,
                        Instant.now(clock)
                    )
                )
                    .then(Mono.just(SmsSendResponse(false, otp, message)))
            }
    }

    override fun findAll(): Flux<Otp> {
        return Flux.from(dsl
            .select(OTP.ID, OTP.PHONE, OTP.OTP_HASH, OTP.TYPE, OTP.CREATED_AT)
            .from(OTP))
            .map { r -> Otp(r.value1()!!, r.value2()!!, r.value3()!!, OtpType.valueOf(r.value4()!!), r.value5()!!) }
    }

    override fun findLastByPhoneAndType(phone: String, otpType: OtpType): Mono<Otp> {
        return Mono.from(dsl
            .select(OTP.ID, OTP.PHONE, OTP.OTP_HASH, OTP.TYPE, OTP.CREATED_AT)
            .from(OTP)
            .where(OTP.PHONE.eq(phone).and(OTP.TYPE.eq(otpType.name)))
            .orderBy(OTP.CREATED_AT.desc())
            .limit(1))
            .map { r -> Otp(r.value1()!!, r.value2()!!, r.value3()!!, OtpType.valueOf(r.value4()!!), r.value5()!!) }
    }
}