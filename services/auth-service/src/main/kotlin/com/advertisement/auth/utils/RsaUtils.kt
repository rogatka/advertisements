package com.advertisement.auth.utils

import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*


object RsaUtils {

    fun getPrivateKey(encodedPrivateKeyString: String): PrivateKey {
        val privateKeyString = String(Base64.getDecoder().decode(encodedPrivateKeyString), StandardCharsets.UTF_8)
        val privateKeyPem = privateKeyString
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s".toRegex(), "")
        val pkcs8EncodedKey: ByteArray = Base64.getDecoder().decode(privateKeyPem)
        val factory: KeyFactory = KeyFactory.getInstance("RSA")
        return factory.generatePrivate(PKCS8EncodedKeySpec(pkcs8EncodedKey))
    }

    fun getPublicKey(encodedPublicKeyString: String): PublicKey {
        val publicKeyString = String(Base64.getDecoder().decode(encodedPublicKeyString), StandardCharsets.UTF_8)
        val publicKeyPem = publicKeyString
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s".toRegex(), "")

        val pkcs8EncodedKey: ByteArray = Base64.getDecoder().decode(publicKeyPem)
        val spec = X509EncodedKeySpec(pkcs8EncodedKey)
        val kf = KeyFactory.getInstance("RSA")
        return kf.generatePublic(spec)
    }
}