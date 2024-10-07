package com.advertisement.auth.controller

import com.advertisement.auth.dto.request.OtpRequest
import com.advertisement.auth.dto.response.SmsSendResponse
import com.advertisement.auth.service.OtpService
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/v1/otp")
class OtpController(val otpService: OtpService) {

    @PostMapping
    fun requestOtp(@Valid @RequestBody otpRequest: OtpRequest,
                   @RequestHeader(name = HttpHeaders.ACCEPT_LANGUAGE) languageHeader: String?)
    : Mono<ResponseEntity<SmsSendResponse>> {
        return otpService.requestOtp(otpRequest, languageHeader?.let { h -> Locale.of(h) }?:Locale.of("RU"))
            .map { r -> ResponseEntity.ok(r) }
    }
}