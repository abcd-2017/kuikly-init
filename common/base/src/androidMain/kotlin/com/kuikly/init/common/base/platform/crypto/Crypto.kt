package com.kuikly.init.common.base.platform.crypto

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Android 加解密实现
 *
 * 基于 javax.crypto.Cipher / MessageDigest / Mac + android.util.Base64。
 */
actual class Crypto {

    actual fun aesEncrypt(plaintext: String, key: String, iv: String?): String? {
        return try {
            val keyBytes = deriveKey(key)
            val ivBytes = deriveIv(key, iv)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(
                Cipher.ENCRYPT_MODE,
                SecretKeySpec(keyBytes, "AES"),
                IvParameterSpec(ivBytes)
            )
            val encrypted = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encrypted, Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }

    actual fun aesDecrypt(ciphertext: String, key: String, iv: String?): String? {
        return try {
            val keyBytes = deriveKey(key)
            val ivBytes = deriveIv(key, iv)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(
                Cipher.DECRYPT_MODE,
                SecretKeySpec(keyBytes, "AES"),
                IvParameterSpec(ivBytes)
            )
            val decoded = Base64.decode(ciphertext, Base64.NO_WRAP)
            val decrypted = cipher.doFinal(decoded)
            String(decrypted, Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    actual fun md5(input: String): String? {
        return try {
            val digest = MessageDigest.getInstance("MD5")
            bytesToHex(digest.digest(input.toByteArray(Charsets.UTF_8)))
        } catch (e: Exception) {
            null
        }
    }

    actual fun sha256(input: String): String? {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            bytesToHex(digest.digest(input.toByteArray(Charsets.UTF_8)))
        } catch (e: Exception) {
            null
        }
    }

    actual fun hmacSha256(data: String, key: String): String? {
        return try {
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(SecretKeySpec(key.toByteArray(Charsets.UTF_8), "HmacSHA256"))
            val hmacBytes = mac.doFinal(data.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(hmacBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }

    actual fun base64Encode(input: String): String? {
        return try {
            Base64.encodeToString(input.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }

    actual fun base64Decode(input: String): String? {
        return try {
            val decoded = Base64.decode(input, Base64.NO_WRAP)
            String(decoded, Charsets.UTF_8)
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
            val digest = MessageDigest.getInstance("SHA-256")
            val hashed = digest.digest(keyBytes)
            hashed.copyOf(32)
        }
    }

    /** IV 处理：如果未提供，用 key 的 MD5 前 16 字节 */
    private fun deriveIv(key: String, iv: String?): ByteArray {
        return if (iv.isNullOrEmpty()) {
            val digest = MessageDigest.getInstance("MD5")
            digest.digest(key.toByteArray(Charsets.UTF_8)).copyOf(16)
        } else {
            val ivBytes = iv.toByteArray(Charsets.UTF_8)
            require(ivBytes.size == 16) { "IV must be exactly 16 bytes, got ${ivBytes.size}" }
            ivBytes
        }
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val sb = StringBuilder(bytes.size * 2)
        for (b in bytes) {
            sb.append(String.format("%02x", b.toInt() and 0xFF))
        }
        return sb.toString()
    }
}

actual fun provideCrypto(): Crypto = Crypto()
