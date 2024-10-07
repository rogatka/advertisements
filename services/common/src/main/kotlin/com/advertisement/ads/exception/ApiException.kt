package com.advertisement.ads.exception

open class ApiException : RuntimeException {
    private val code: String?

    constructor(message: String) : super(message) {
        this.code = null
    }

    constructor(message: String, cause: Throwable) : super(message, cause) {
        this.code = null
    }

    constructor(message: String, code: String, cause: Throwable) : super(message, cause) {
        this.code = code
    }
}