package com.advertisement.ads.controller

import com.advertisement.ads.dto.response.FileResponse
import com.advertisement.ads.model.FileType
import com.advertisement.ads.service.FileService
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/files")
class FileController(
    val fileService: FileService
) {

    @PostMapping("/images", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(@RequestPart("files") files: Flux<FilePart>): ResponseEntity<Flux<FileResponse>> {
        return ResponseEntity.ok(files.flatMap{ f ->
            DataBufferUtils.join(f.content())
            .map{dataBuffer -> dataBuffer.asByteBuffer().array()}
            .flatMap { b -> fileService.saveFile(b, FileType.IMAGE, f.filename()) }
            .map { id -> FileResponse(id.toString(), f.filename()) }})
    }

    @GetMapping("/images/{id}")
    fun getFile(@PathVariable("id") id: String): Mono<ResponseEntity<ByteArray>> {
        return fileService.getFile(id, FileType.IMAGE)
            .map { r ->
                ResponseEntity
                    .status(HttpStatus.OK)
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpg")
                    .body(r)
            }
    }
}