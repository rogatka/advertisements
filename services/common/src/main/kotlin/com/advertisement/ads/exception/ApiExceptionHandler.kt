package com.advertisement.ads.exception

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import reactor.core.publisher.Mono
import java.time.Clock
import java.time.LocalDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class ApiExceptionHandler(
    private val clock: Clock
) {

    @ExceptionHandler(Exception::class)
    fun handleCommonException(exception: Exception): Mono<ResponseEntity<ApiErrorResponse>> {
        logger.error(exception.message, exception)
        val errorResponse = ApiErrorResponse(exception.message?: "Unknown Error", LocalDateTime.now(clock))
        return Mono.just(ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR))
    }

    @ExceptionHandler(ApiNotFoundException::class)
    fun handleNotFoundException(exception: ApiNotFoundException): Mono<ResponseEntity<ApiErrorResponse>> {
        logger.error(exception.message, exception)
        val errorResponse = ApiErrorResponse(exception.message?: "Required element not found", LocalDateTime.now(clock))
        return Mono.just(ResponseEntity(errorResponse, HttpStatus.NOT_FOUND))
    }

    @ExceptionHandler(ApiBadRequestException::class)
    fun handleBadRequestException(exception: ApiBadRequestException): Mono<ResponseEntity<ApiErrorResponse>> {
        logger.error(exception.message, exception)
        val errorResponse = ApiErrorResponse(exception.message?: "Bad request", LocalDateTime.now(clock))
        return Mono.just(ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST))
    }

    @ExceptionHandler(AccessDeniedException::class, ApiForbiddenException::class)
    fun handleAccessDeniedException(exception: Exception): Mono<ResponseEntity<ApiErrorResponse>> {
        logger.error(exception.message, exception)
        val errorResponse = ApiErrorResponse(exception.message?: "Access denied", LocalDateTime.now(clock))
        return Mono.just(ResponseEntity(errorResponse, HttpStatus.FORBIDDEN))
    }

    @ExceptionHandler(ApiInternalServerErrorException::class)
    fun handleInternalServerErrorException(exception: ApiInternalServerErrorException): Mono<ResponseEntity<ApiErrorResponse>> {
        logger.error(exception.message, exception)
        val errorResponse = ApiErrorResponse(exception.message?: "Server error", LocalDateTime.now(clock))
        return Mono.just(ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR))
    }

    @ExceptionHandler(ApiServiceUnavailableException::class)
    fun handleInternalServiceUnavailableException(exception: ApiServiceUnavailableException): Mono<ResponseEntity<ApiErrorResponse>> {
        logger.error(exception.message, exception)
        val errorResponse = ApiErrorResponse(exception.message?: "Service unavailable", LocalDateTime.now(clock))
        return Mono.just(ResponseEntity(errorResponse, HttpStatus.SERVICE_UNAVAILABLE))
    }

    @ExceptionHandler(ApiUnauthorizedException::class)
    fun handleUnauthorizedException(exception: ApiUnauthorizedException): Mono<ResponseEntity<ApiErrorResponse>> {
        logger.error(exception.message, exception)
        val errorResponse = ApiErrorResponse(exception.message?: "Unauthorized", LocalDateTime.now(clock))
        return Mono.just(ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED))
    }

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleWebExchangeBindException(exception: WebExchangeBindException): Mono<ResponseEntity<ApiErrorResponse>> {
        logger.error(exception.message, exception)
        val errors: MutableMap<String?, Any?> = convert(exception)
        val errorResponse = ApiErrorResponse(errors.toString(), LocalDateTime.now(clock))
        return Mono.just(ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST))
    }

    @ExceptionHandler(ApiException::class)
    fun handleApiException(exception: ApiException): Mono<ResponseEntity<ApiErrorResponse>> {
        logger.error(exception.message, exception)
        val errorResponse = ApiErrorResponse(exception.message?: "Server error", LocalDateTime.now(clock))
        return Mono.just(ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR))
    }

    private fun convert(exception: WebExchangeBindException): MutableMap<String?, Any?> {
        val errorMap: MutableMap<String?, Any?> = HashMap()
        for (error in exception.bindingResult.allErrors) {
            if (error is FieldError) {
                val fieldError = error
                errorMap[fieldError.field] = error.getDefaultMessage()
                var defaultMessage = Optional.ofNullable(fieldError.defaultMessage)
                    .orElse(String.format("Invalid value: '%s'", fieldError.rejectedValue))
                // in case of 'typeMismatch'
                if (defaultMessage.contains("Failed to convert")) {
                    defaultMessage = String.format("Invalid value: '%s'", fieldError.rejectedValue)
                }
                errorMap[fieldError.field] = defaultMessage
            } else {
                errorMap[error.code] = error.defaultMessage
            }
        }
        return errorMap
    }
}