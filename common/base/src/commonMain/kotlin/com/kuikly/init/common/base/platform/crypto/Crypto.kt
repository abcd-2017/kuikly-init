package com.kuikly.init.common.base.platform.crypto

/**
 * 加解密能力抽象
 *
 * 提供 AES / MD5 / SHA-256 / HMAC-SHA256 / Base64 等常用加解密功能。
 */
expect class Crypto {
    /** AES-256-CBC 加密，返回 Base64 编码密文，失败返回 null */
    fun aesEncrypt(plaintext: String, key: String, iv: String? = null): String?

    /** AES-256-CBC 解密，失败返回 null */
    fun aesDecrypt(ciphertext: String, key: String, iv: String? = null): String?

    /** MD5 哈希（32 位小写 hex），失败返回 null */
    fun md5(input: String): String?

    /** SHA-256 哈希（64 位小写 hex），失败返回 null */
    fun sha256(input: String): String?

    /** HMAC-SHA256（Base64 编码），失败返回 null */
    fun hmacSha256(data: String, key: String): String?

    /** Base64 编码，失败返回 null */
    fun base64Encode(input: String): String?

    /** Base64 解码，失败返回 null */
    fun base64Decode(input: String): String?
}

/** 全局访问入口 */
expect fun provideCrypto(): Crypto
