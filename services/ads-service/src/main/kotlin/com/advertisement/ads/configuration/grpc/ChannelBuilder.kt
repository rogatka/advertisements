package com.advertisement.ads.configuration.grpc

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.micrometer.core.instrument.binder.grpc.ObservationGrpcClientInterceptor
import io.micrometer.observation.ObservationRegistry
import org.springframework.stereotype.Component

@Component
class ChannelBuilder(
    private val grpcClientRequestInterceptor: GrpcClientRequestInterceptor,
    private val observationRegistry: ObservationRegistry
) {

    fun forTarget(target: String?): ManagedChannel {
        return ManagedChannelBuilder.forTarget(target)
            .intercept(grpcClientRequestInterceptor, ObservationGrpcClientInterceptor(observationRegistry))
            .usePlaintext()
            .build()
    }
}