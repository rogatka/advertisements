package com.advertisement.auth.utils

import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

private const val HASHING_ALGORITHM = "PBKDF2WithHmacSHA1"

object Utils {

    fun generateCode(): String {
        val randomNumber = SecureRandom().nextInt(10000)
        val otp = String.format("%04d", randomNumber)
        return otp
    }

    fun calculateHash(input: String, saltBytes: ByteArray): ByteArray {
        val spec: KeySpec = PBEKeySpec(input.toCharArray(), saltBytes, 65536, 128)
        val factory = SecretKeyFactory.getInstance(HASHING_ALGORITHM)
        val hashedInput = factory.generateSecret(spec).encoded
        return hashedInput
    }
}