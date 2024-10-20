package com.advertisement.ads.configuration.grpc

import com.advertisement.ads.configuration.AuthServiceProperties
import io.grpc.*
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*

private val logger = KotlinLogging.logger {}

@Component
class GrpcClientRequestInterceptor(
    val authServiceProperties: AuthServiceProperties
): ClientInterceptor {

    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        methodDescriptor: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        channel: Channel
    ): ClientCall<ReqT, RespT> {
        logger.debug("Called: {}", methodDescriptor.fullMethodName)
        return object : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
            channel.newCall<ReqT, RespT>(
                methodDescriptor,
                callOptions
            )
        ) {
            override fun start(respTListener: Listener<RespT>, headers: Metadata) {
                val auth: String = authServiceProperties.username + ":" + authServiceProperties.password
                val base64Creds = Base64.getEncoder().encodeToString(auth.toByteArray(StandardCharsets.UTF_8))
                headers.put(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER), "Basic $base64Creds")
                super.start(respTListener, headers)
            }
        }
    }
}