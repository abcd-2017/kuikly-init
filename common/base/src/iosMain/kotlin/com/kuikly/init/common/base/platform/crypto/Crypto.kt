package com.kuikly.init.common.base.platform.crypto

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create

/**
 * iOS 加解密实现
 *
 * AES 使用纯 Kotlin 实现（PureKotlinCrypto.AES256），不依赖 CommonCrypto。
 * TODO: 后续可通过 cinterop 调用 CommonCrypto CCCryptor 替换，获得更好性能。
 * MD5/SHA-256/HMAC-SHA256/Base64 同样为纯 Kotlin 实现。
 */
actual class Crypto {

    actual fun aesEncrypt(plaintext: String, key: String, iv: String?): String? {
        return try {
            val keyBytes = deriveKey(key)
            val ivBytes = deriveIv(key, iv)
            val encrypted = AES256.encrypt(plaintext.toByteArray(Charsets.UTF_8), keyBytes, ivBytes)
            encrypted.toNSData().base64EncodedStringWithOptions(0uL)
        } catch (e: Exception) {
            null
        }
    }

    actual fun aesDecrypt(ciphertext: String, key: String, iv: String?): String? {
        return try {
            val keyBytes = deriveKey(key)
            val ivBytes = deriveIv(key, iv)
            val decoded = nsDataFromBase64(ciphertext) ?: return null
            val decodedBytes = decoded.toByteArray()
            val decrypted = AES256.decrypt(decodedBytes, keyBytes, ivBytes)
            String(decrypted, Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    actual fun md5(input: String): String? {
        return try {
            val bytes = input.toByteArray(Charsets.UTF_8)
            val digest = MD5.md5(bytes)
            digest.joinToString("") { "%02x".format(it.toInt() and 0xFF) }
        } catch (e: Exception) {
            null
        }
    }

    actual fun sha256(input: String): String? {
        return try {
            val bytes = input.toByteArray(Charsets.UTF_8)
            val digest = SHA256.sha256(bytes)
            digest.joinToString("") { "%02x".format(it.toInt() and 0xFF) }
        } catch (e: Exception) {
            null
        }
    }

    actual fun hmacSha256(data: String, key: String): String? {
        return try {
            val dataBytes = data.toByteArray(Charsets.UTF_8)
            val keyBytes = key.toByteArray(Charsets.UTF_8)
            val hmacBytes = HMAC_SHA256.hmacSha256(keyBytes, dataBytes)
            hmacBytes.toNSData().base64EncodedStringWithOptions(0uL)
        } catch (e: Exception) {
            null
        }
    }

    actual fun base64Encode(input: String): String? {
        return try {
            input.toByteArray(Charsets.UTF_8).toNSData()
                .base64EncodedStringWithOptions(0uL)
        } catch (e: Exception) {
            null
        }
    }

    actual fun base64Decode(input: String): String? {
        return try {
            val data = nsDataFromBase64(input) ?: return null
            val length = data.length.toInt()
            val byteArray = ByteArray(length)
            data.bytes?.let { ptr ->
                for (i in 0 until length) {
                    byteArray[i] = ptr.reinterpret<ByteVar>()[i]
                }
            } ?: return null
            String(byteArray, Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    /** 密钥处理：不足 32 字节用 SHA-256 哈希后取前 32 字节 */
    private fun deriveKey(key: String): ByteArray {
        val keyBytes = key.toByteArray(Charsets.UTF_8)
        return if (keyBytes.size == 32) {
            keyBytes
        } else {
            SHA256.sha256(keyBytes).copyOf(32)
        }
    }

    /** IV 处理：如果未提供，用 key 的 MD5 前 16 字节；提供时必须恰好 16 字节 */
    private fun deriveIv(key: String, iv: String?): ByteArray {
        return if (iv.isNullOrEmpty()) {
            MD5.md5(key.toByteArray(Charsets.UTF_8)).copyOf(16)
        } else {
            val ivBytes = iv.toByteArray(Charsets.UTF_8)
            require(ivBytes.size == 16) { "IV must be exactly 16 bytes, got ${ivBytes.size}" }
            ivBytes
        }
    }

    private fun ByteArray.toNSData(): NSData = memScoped {
        this@toNSData.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = this@toNSData.size.toULong())
        }
    }

    private fun nsDataFromBase64(base64: String): NSData? {
        return NSData.create(base64EncodedString = base64, options = 0uL)
    }

    private fun NSData.toByteArray(): ByteArray {
        val length = this.length.toInt()
        val byteArray = ByteArray(length)
        this.bytes?.let { ptr ->
            for (i in 0 until length) {
                byteArray[i] = ptr.reinterpret<ByteVar>()[i]
            }
        }
        return byteArray
    }
}

actual fun provideCrypto(): Crypto = Crypto()
