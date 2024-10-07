package com.advertisement.ads.repository

import com.advertisement.ads.model.File
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface FileRepository : ReactiveMongoRepository<File, String> {
}
