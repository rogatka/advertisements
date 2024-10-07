package com.advertisement.auth.service

import com.advertisement.auth.dto.request.OtpRequest
import com.advertisement.auth.dto.OtpType
import com.advertisement.auth.dto.response.SmsSendResponse
import com.advertisement.auth.model.Otp
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface OtpService {
    fun requestOtp(otpRequest: OtpRequest, locale: Locale): Mono<SmsSendResponse>
    fun findAll(): Flux<Otp>
    fun findLastByPhoneAndType(phone: String, otpType: OtpType): Mono<Otp>
}
