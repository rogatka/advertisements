package com.advertisement.ads

import io.micrometer.tracing.Span
import io.micrometer.tracing.Tracer
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import io.prometheus.metrics.tracer.common.SpanContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Hooks

@SpringBootApplication(scanBasePackages = ["com.advertisement.ads"])
@ConfigurationPropertiesScan
class AdsApplication

    fun main(args: Array<String>) {
        //To be able to see traceId and spanId in logs in reactive application
        Hooks.enableAutomaticContextPropagation();
        runApplication<AdsApplication>(*args)
    }

@Configuration
class TracingConfig {

    @Bean
    fun otlpHttpSpanExporter(@Value("\${tracing.url}") url: String): OtlpGrpcSpanExporter {
        return OtlpGrpcSpanExporter.builder().setEndpoint(url).build();
    }
}

// to avoid app's freeze on start up after adding the 'micrometer-prometheus' dependency
// https://github.com/spring-projects/spring-framework/issues/32996
@Configuration
class MicrometerConfig {

    @Bean
    fun spanContext(tracer: Tracer): SpanContext {
        return TracerSpanContext(tracer)
    }

    class TracerSpanContext (val tracer: Tracer) : SpanContext {

        override fun getCurrentTraceId(): String? {
            val currentSpan = currentSpan()
            return if ((currentSpan != null)) currentSpan.context().traceId() else null
        }

        override fun getCurrentSpanId(): String? {
            val currentSpan = currentSpan()
            return if ((currentSpan != null)) currentSpan.context().spanId() else null
        }

        override fun isCurrentSpanSampled(): Boolean {
            val currentSpan = currentSpan() ?: return false
            val sampled = currentSpan.context().sampled()
            return sampled != null && sampled
        }

        override fun markCurrentSpanAsExemplar() {
        }

        private fun currentSpan(): Span? {
            return this.tracer.currentSpan()
        }
    }
}