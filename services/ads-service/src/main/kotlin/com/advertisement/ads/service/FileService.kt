package com.advertisement.ads.service

import com.advertisement.ads.model.FileType
import reactor.core.publisher.Mono

interface FileService {

    fun saveFile(file: ByteArray, fileType: FileType, fileName: String): Mono<String>
    fun getFile(id: String, fileType: FileType): Mono<ByteArray>
    fun deleteFile(id: String): Mono<Boolean>
}