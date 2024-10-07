package com.advertisement.auth.service

import com.advertisement.auth.dto.response.SmsServiceSendSmsResponse
import reactor.core.publisher.Mono

interface SmsService {
    fun sendSms(phone: String, message: String): Mono<SmsServiceSendSmsResponse>
}
