package com.advertisement.ads.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import java.time.Duration

@Configuration
@EnableCaching
class RedisConfiguration {

    @Bean
    fun cacheConfiguration(cachingProperties: CachingProperties, objectMapper: ObjectMapper): RedisCacheConfiguration {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(cachingProperties.defaultTtlMinutes.toLong()))
            .disableCachingNullValues()
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                GenericJackson2JsonRedisSerializer(objectMapper)
            ));
    }

    @Bean
    fun redisCacheManagerBuilderCustomizer(cachingProperties: CachingProperties, objectMapper: ObjectMapper): RedisCacheManagerBuilderCustomizer {
        return RedisCacheManagerBuilderCustomizer { builder ->
            builder
                .withCacheConfiguration(
                    cachingProperties.advertisements.cacheName,
                    RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(cachingProperties.advertisements.ttlMinutes.toLong()))
                )
                .withCacheConfiguration(
                    cachingProperties.cities.cacheName,
                    RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(cachingProperties.cities.ttlMinutes.toLong()))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                            GenericJackson2JsonRedisSerializer(objectMapper)
                        ))
                )
        }
    }
}