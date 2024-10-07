package com.advertisement.ads.model

import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.*

@Document(collection = "files")
data class File (
    var id: String?,
    val fileStorageId: UUID?,
    val fileName: String,
    val createdAt: Instant
)