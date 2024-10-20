package com.advertisement.auth.exception

import io.grpc.Status
import mu.KotlinLogging
import org.lognet.springboot.grpc.recovery.GRpcExceptionHandler
import org.lognet.springboot.grpc.recovery.GRpcExceptionScope
import org.lognet.springboot.grpc.recovery.GRpcServiceAdvice
import org.springframework.web.bind.support.WebExchangeBindException

private val logger = KotlinLogging.logger {}

@GRpcServiceAdvice
class GrpcExceptionHandler {

    @GRpcExceptionHandler
    fun handleCommonException(exception: Exception, scope: GRpcExceptionScope): Status {
        logger.error(exception.message, exception)
        return Status.INTERNAL
    }

    @GRpcExceptionHandler
    fun handleNotFoundException(exception: ApiNotFoundException, scope: GRpcExceptionScope): Status {
        logger.error(exception.message, exception)
        return Status.NOT_FOUND
    }

    @GRpcExceptionHandler
    fun handleAccessDeniedException(exception: AccessDeniedException, scope: GRpcExceptionScope): Status {
        logger.error(exception.message, exception)
        return Status.PERMISSION_DENIED
    }

    @GRpcExceptionHandler
    fun handleApiOtpExpiredException(exception: ApiOtpExpiredException, scope: GRpcExceptionScope): Status {
        logger.error(exception.message, exception)
        return Status.PERMISSION_DENIED
    }

    @GRpcExceptionHandler
    fun handleApiForbiddenException(exception: ApiForbiddenException, scope: GRpcExceptionScope): Status {
        logger.error(exception.message, exception)
        return Status.PERMISSION_DENIED
    }

    @GRpcExceptionHandler
    fun handleInternalServerErrorException(exception: ApiInternalServerErrorException, scope: GRpcExceptionScope): Status {
        logger.error(exception.message, exception)
        return Status.INTERNAL
    }

    @GRpcExceptionHandler
    fun handleInternalServiceUnavailableException(exception: ApiServiceUnavailableException, scope: GRpcExceptionScope): Status {
        logger.error(exception.message, exception)
        return Status.UNAVAILABLE
    }

    @GRpcExceptionHandler
    fun handleUnauthorizedException(exception: ApiUnauthorizedException, scope: GRpcExceptionScope): Status {
        logger.error(exception.message, exception)
        return Status.UNAUTHENTICATED
    }

    @GRpcExceptionHandler
    fun handleWebExchangeBindException(exception: WebExchangeBindException, scope: GRpcExceptionScope): Status {
        logger.error(exception.message, exception)
        return Status.INVALID_ARGUMENT
    }

    @GRpcExceptionHandler
    fun handleApiException(exception: ApiException, scope: GRpcExceptionScope): Status {
        logger.error(exception.message, exception)
        return Status.INTERNAL
    }
}