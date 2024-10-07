package com.advertisement.ads.configuration

import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val logger = KotlinLogging.logger {}

@Configuration
class MinioConfiguration {

    @Bean
    fun minioClient(fileStorageProperties: MinioProperties): MinioClient {
        val bucketName: String = fileStorageProperties.bucketName
        val client: MinioClient = MinioClient.builder().endpoint(fileStorageProperties.url)
            .credentials(fileStorageProperties.accessKey, fileStorageProperties.secretKey).build()
        if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            logger.info("Bucket '{}' not exists. Creating a bucket...", bucketName)
            client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
            logger.info("Bucket '{}' is created", bucketName)
        }
        return client
    }
}
