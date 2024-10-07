package com.advertisement.ads.service

import com.advertisement.ads.configuration.MinioProperties
import com.advertisement.ads.exception.ApiInternalServerErrorException
import com.advertisement.ads.exception.ApiNotFoundException
import com.advertisement.ads.exception.ApiServiceUnavailableException
import com.advertisement.ads.model.File
import com.advertisement.ads.model.FileType
import com.advertisement.ads.repository.FileRepository
import com.advertisement.ads.utils.FileUtils
import com.google.common.io.ByteStreams
import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import io.minio.errors.MinioException
import mu.KotlinLogging
import okio.IOException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.io.ByteArrayInputStream
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.*

private const val DIRECTORY_IMAGES = "images"

private val logger = KotlinLogging.logger {}

@Service
class FileServiceImpl(
    val fileRepository: FileRepository,
    val minioClient: MinioClient,
    val minioProperties: MinioProperties,
    val clock: Clock
) : FileService {

    override fun saveFile(file: ByteArray, fileType: FileType, fileName: String): Mono<String> {
        val directory = when (fileType) {
            FileType.IMAGE -> DIRECTORY_IMAGES
        }
        val fileStorageId = UUID.randomUUID()
        return Mono.just(
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(minioProperties.bucketName)
                    .`object`(directory?.let { "/$directory/$fileStorageId" } ?: fileStorageId.toString())
                    .stream(ByteArrayInputStream(file), file.size.toLong(), -1)
                    .contentType(FileUtils.defineFileContentType(file))
                    .build()
            )
        ).flatMap { _ -> fileRepository.save(File(null, fileStorageId, fileName, Instant.now(clock))) }
            .map(File::id)
    }

    override fun getFile(id: String, fileType: FileType): Mono<ByteArray> {
        val directory = when (fileType) {
            FileType.IMAGE -> "images"
        }
        return fileRepository.findById(id)
            .switchIfEmpty(Mono.error(ApiNotFoundException("Requested file not found")))
            .map { file ->
                val stream = minioClient.getObject(
                    GetObjectArgs.builder()
                        .bucket(minioProperties.bucketName)
                        .`object`(directory?.let { "/$directory/${file.fileStorageId.toString()}" }
                            ?: file.fileStorageId.toString())
                        .build()
                )
                ByteStreams.toByteArray(stream)
            }
            .onErrorResume({ e -> e is MinioException || e is IOException },
                { e ->
                    logger.error("Server error: ${e.message}")
                    Mono.error(ApiServiceUnavailableException("Auth service is unavailable: ${e.message}"))
                }
            )
            .retryWhen(
                Retry.backoff(
                    minioProperties.retry.maxAttempts.toLong(),
                    Duration.ofSeconds(minioProperties.retry.minBackoffSeconds.toLong())
                )
                    .filter { throwable -> throwable is ApiInternalServerErrorException }
                    .onRetryExhaustedThrow { retryBackoffSpec, retrySignal -> ApiServiceUnavailableException("Auth service is unavailable") })
    }

    override fun deleteFile(id: String): Mono<Boolean> {
        return fileRepository.findById(id)
            .switchIfEmpty(Mono.error(ApiNotFoundException("Requested file not found")))
            .map { file ->
                minioClient.removeObject(
                    RemoveObjectArgs.builder()
                        .bucket(minioProperties.bucketName)
                        .`object`(file.fileStorageId.toString())
                        .build()
                )
                true
            }
            .onErrorResume({ e -> e is MinioException || e is IOException },
                { e ->
                    logger.error("Server error: ${e.message}")
                    Mono.error(ApiServiceUnavailableException("Auth service is unavailable: ${e.message}"))
                }
            )
            .retryWhen(
                Retry.backoff(
                    minioProperties.retry.maxAttempts.toLong(),
                    Duration.ofSeconds(minioProperties.retry.minBackoffSeconds.toLong())
                )
                    .filter { throwable -> throwable is ApiInternalServerErrorException }
                    .onRetryExhaustedThrow { retryBackoffSpec, retrySignal -> ApiServiceUnavailableException("Auth service is unavailable") })
            .onErrorResume { e ->
                logger.error("Unknown error: ${e.message}")
                Mono.just(false)
            }
    }
}