package com.kuikly.init.common.base.platform.crypto

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

/**
 * OHOS 加解密实现（KNOI 方式）
 *
 * 通过 KNOI 调用 ETS 侧 @ohos.security.cryptoFramework 实现。
 */
actual class Crypto {
    private val service get() = getIOHOSPlatformServiceApi()

    actual fun aesEncrypt(plaintext: String, key: String, iv: String?): String? =
        service?.aesEncrypt(plaintext, key, iv)

    actual fun aesDecrypt(ciphertext: String, key: String, iv: String?): String? =
        service?.aesDecrypt(ciphertext, key, iv)

    actual fun md5(input: String): String? =
        service?.md5(input)

    actual fun sha256(input: String): String? =
        service?.sha256(input)

    actual fun hmacSha256(data: String, key: String): String? =
        service?.hmacSha256(data, key)

    actual fun base64Encode(input: String): String? =
        service?.base64Encode(input)

    actual fun base64Decode(input: String): String? =
        service?.base64Decode(input)
}

actual fun provideCrypto(): Crypto = Crypto()
