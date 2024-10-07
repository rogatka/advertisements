package com.advertisement.payment.configuration

import com.advertisement.payment.configuration.handler.PageableHandlerMethodArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer


@Configuration
class WebConfig : WebFluxConfigurer {

    // to be able to validate controller's method arguments
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(PageableHandlerMethodArgumentResolver())
    }
}