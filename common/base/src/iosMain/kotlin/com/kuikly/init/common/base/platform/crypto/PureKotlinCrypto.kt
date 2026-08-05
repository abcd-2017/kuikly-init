package com.kuikly.init.common.base.platform.crypto

/**
 * 纯 Kotlin 实现的 MD5 / SHA-256 / HMAC-SHA256
 *
 * 用于 iOS 平台（待 cinterop 配置后 AES 可切换为 CommonCrypto）。
 */

private val MD5_K = intArrayOf(
    0xd76aa478, 0xe8c7b756, 0x242070db, 0xc1bdceee,
    0xf57c0faf, 0x4787c62a, 0xa8304613, 0xfd469501,
    0x698098d8, 0x8b44f7af, 0xffff5bb1, 0x895cd7be,
    0x6b901122, 0xfd987193, 0xa679438e, 0x49b40821,
    0xf61e2562, 0xc040b340, 0x265e5a51, 0xe9b6c7aa,
    0xd62f105d, 0x02441453, 0xd8a1e681, 0xe7d3fbc8,
    0x21e1cde6, 0xc33707d6, 0xf4d50d87, 0x455a14ed,
    0xa9e3e905, 0xfcefa3f8, 0x676f02d9, 0x8d2a4c8a,
    0xfffa3942, 0x8771f681, 0x6d9d6122, 0xfde5380c,
    0xa4beea44, 0x4bdecfa9, 0xf6bb4b60, 0xbebfbc70,
    0x289b7ec6, 0xeaa127fa, 0xd4ef3085, 0x04881d05,
    0xd9d4d039, 0xe6db99e5, 0x1fa27cf8, 0xc4ac5665,
    0xf4292244, 0x432aff97, 0xab9423a7, 0xfc93a039,
    0x655b59c3, 0x8f0ccc92, 0xffeff47d, 0x85845dd1,
    0x6fa87e4f, 0xfe2ce6e0, 0xa3014314, 0x4e0811a1,
    0xf7537e82, 0xbd3af235, 0x2ad7d2bb, 0xeb86d391
)

private val MD5_S = intArrayOf(
    7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22,
    5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20,
    4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23,
    6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21
)

internal object MD5 {
    fun md5(input: ByteArray): ByteArray {
        val originalLength = input.size
        val bitLength = originalLength * 8

        // Padding: append 0x80, then zeros, then 64-bit length
        val paddedLength = ((originalLength + 8) / 64 + 1) * 64
        val padded = ByteArray(paddedLength)
        System.arraycopy(input, 0, padded, 0, originalLength)
        padded[originalLength] = 0x80.toByte()

        // Append length in bits as 64-bit little-endian
        for (i in 0 until 8) {
            padded[paddedLength - 8 + i] = (bitLength.toLong() shr (i * 8)).toByte()
        }

        var a0 = 0x67452301
        var b0 = 0xefcdab89
        var c0 = 0x98badcfe
        var d0 = 0x10325476

        for (offset in 0 until paddedLength step 64) {
            val m = IntArray(16)
            for (j in 0 until 16) {
                val idx = offset + j * 4
                m[j] = (padded[idx].toInt() and 0xFF) or
                        ((padded[idx + 1].toInt() and 0xFF) shl 8) or
                        ((padded[idx + 2].toInt() and 0xFF) shl 16) or
                        ((padded[idx + 3].toInt() and 0xFF) shl 24)
            }

            var a = a0
            var b = b0
            var c = c0
            var d = d0

            for (i in 0 until 64) {
                val f: Int
                val g: Int
                when {
                    i < 16 -> {
                        f = (b and c) or (b.inv() and d)
                        g = i
                    }
                    i < 32 -> {
                        f = (d and b) or (d.inv() and c)
                        g = (5 * i + 1) % 16
                    }
                    i < 48 -> {
                        f = b xor c xor d
                        g = (3 * i + 5) % 16
                    }
                    else -> {
                        f = c xor (b or d.inv())
                        g = (7 * i) % 16
                    }
                }
                val temp = d
                d = c
                c = b
                b = b + leftRotate((a + f + MD5_K[i] + m[g]), MD5_S[i])
                a = temp
            }

            a0 += a
            b0 += b
            c0 += c
            d0 += d
        }

        val result = ByteArray(16)
        var idx = 0
        for (value in intArrayOf(a0, b0, c0, d0)) {
            result[idx++] = (value and 0xFF).toByte()
            result[idx++] = ((value shr 8) and 0xFF).toByte()
            result[idx++] = ((value shr 16) and 0xFF).toByte()
            result[idx++] = ((value shr 24) and 0xFF).toByte()
        }
        return result
    }

    private fun leftRotate(x: Int, c: Int): Int {
        return (x shl c) or (x ushr (32 - c))
    }
}

private val SHA256_K = intArrayOf(
    0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5,
    0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
    0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3,
    0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
    0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc,
    0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
    0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7,
    0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
    0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
    0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
    0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3,
    0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
    0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5,
    0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
    0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208,
    0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
)

internal object SHA256 {
    fun sha256(input: ByteArray): ByteArray {
        val originalLength = input.size
        val bitLength = originalLength * 8L

        val paddedLength = ((originalLength + 8) / 64 + 1) * 64
        val padded = ByteArray(paddedLength)
        System.arraycopy(input, 0, padded, 0, originalLength)
        padded[originalLength] = 0x80.toByte()

        for (i in 0 until 8) {
            padded[paddedLength - 8 + i] = (bitLength shr (i * 8)).toByte()
        }

        var h0 = 0x6a09e667
        var h1 = 0xbb67ae85
        var h2 = 0x3c6ef372
        var h3 = 0xa54ff53a
        var h4 = 0x510e527f
        var h5 = 0x9b05688c
        var h6 = 0x1f83d9ab
        var h7 = 0x5be0cd19

        for (offset in 0 until paddedLength step 64) {
            val w = IntArray(64)
            for (i in 0 until 16) {
                val idx = offset + i * 4
                w[i] = ((padded[idx].toInt() and 0xFF) shl 24) or
                        ((padded[idx + 1].toInt() and 0xFF) shl 16) or
                        ((padded[idx + 2].toInt() and 0xFF) shl 8) or
                        (padded[idx + 3].toInt() and 0xFF)
            }
            for (i in 16 until 64) {
                val s0 = rightRotate(w[i - 15], 7) xor rightRotate(w[i - 15], 18) xor (w[i - 15] ushr 3)
                val s1 = rightRotate(w[i - 2], 17) xor rightRotate(w[i - 2], 19) xor (w[i - 2] ushr 10)
                w[i] = w[i - 16] + s0 + w[i - 7] + s1
            }

            var a = h0
            var b = h1
            var c = h2
            var d = h3
            var e = h4
            var f = h5
            var g = h6
            var h = h7

            for (i in 0 until 64) {
                val s1 = rightRotate(e, 6) xor rightRotate(e, 11) xor rightRotate(e, 25)
                val ch = (e and f) xor (e.inv() and g)
                val temp1 = h + s1 + ch + SHA256_K[i] + w[i]
                val s0 = rightRotate(a, 2) xor rightRotate(a, 13) xor rightRotate(a, 22)
                val maj = (a and b) xor (a and c) xor (b and c)
                val temp2 = s0 + maj

                h = g
                g = f
                f = e
                e = d + temp1
                d = c
                c = b
                b = a
                a = temp1 + temp2
            }

            h0 += a
            h1 += b
            h2 += c
            h3 += d
            h4 += e
            h5 += f
            h6 += g
            h7 += h
        }

        val result = ByteArray(32)
        var idx = 0
        for (value in intArrayOf(h0, h1, h2, h3, h4, h5, h6, h7)) {
            result[idx++] = ((value shr 24) and 0xFF).toByte()
            result[idx++] = ((value shr 16) and 0xFF).toByte()
            result[idx++] = ((value shr 8) and 0xFF).toByte()
            result[idx++] = (value and 0xFF).toByte()
        }
        return result
    }

    private fun rightRotate(x: Int, n: Int): Int {
        return (x ushr n) or (x shl (32 - n))
    }
}

internal object HMAC_SHA256 {
    private const val BLOCK_SIZE = 64

    fun hmacSha256(key: ByteArray, data: ByteArray): ByteArray {
        var actualKey = key
        if (actualKey.size > BLOCK_SIZE) {
            actualKey = SHA256.sha256(actualKey)
        }

        val paddedKey = ByteArray(BLOCK_SIZE)
        System.arraycopy(actualKey, 0, paddedKey, 0, actualKey.size)

        val ipad = ByteArray(BLOCK_SIZE) { i -> (paddedKey[i] xor 0x36).toByte() }
        val opad = ByteArray(BLOCK_SIZE) { i -> (paddedKey[i] xor 0x5c).toByte() }

        val inner = ByteArray(BLOCK_SIZE + data.size)
        System.arraycopy(ipad, 0, inner, 0, BLOCK_SIZE)
        System.arraycopy(data, 0, inner, BLOCK_SIZE, data.size)
        val innerHash = SHA256.sha256(inner)

        val outer = ByteArray(BLOCK_SIZE + 32)
        System.arraycopy(opad, 0, outer, 0, BLOCK_SIZE)
        System.arraycopy(innerHash, 0, outer, BLOCK_SIZE, 32)
        return SHA256.sha256(outer)
    }
}

// ==================== AES-256-CBC (纯 Kotlin 实现) ====================

/**
 * 纯 Kotlin AES-256-CBC 实现
 *
 * 不依赖 CommonCrypto，用于 iOS 平台。
 * TODO: 后续可通过 cinterop 调用 CommonCrypto CCCryptor 替换，获得更好性能。
 */
internal object AES256 {
    private const val NB = 4   // 分组大小（32-bit 字）
    private const val NK = 8   // 密钥长度（256-bit = 8 个字）
    private const val NR = 14  // 轮数

    // S-Box
    private val SBOX = intArrayOf(
        0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
        0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
        0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
        0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
        0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
        0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
        0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
        0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
        0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
        0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
        0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
        0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
        0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
        0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
        0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
        0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16
    )

    // 逆 S-Box
    private val INV_SBOX = intArrayOf(
        0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb,
        0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb,
        0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e,
        0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25,
        0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92,
        0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84,
        0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06,
        0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b,
        0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73,
        0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e,
        0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b,
        0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4,
        0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f,
        0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef,
        0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61,
        0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d
    )

    // 轮常数
    private val RCON = intArrayOf(
        0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36
    )

    /**
     * AES-256-CBC 加密，返回 Base64 编码密文
     */
    fun encrypt(plaintext: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        val expandedKey = keyExpansion(key)
        val padded = pkcs7Pad(plaintext, 16)
        val ciphertext = ByteArray(padded.size)
        var prevBlock = iv.copyOf()

        for (i in padded.indices step 16) {
            val block = ByteArray(16)
            for (j in 0 until 16) {
                block[j] = (padded[i + j] xor prevBlock[j])
            }
            val encrypted = encryptBlock(block, expandedKey)
            System.arraycopy(encrypted, 0, ciphertext, i, 16)
            prevBlock = encrypted
        }

        return ciphertext
    }

    /**
     * AES-256-CBC 解密
     */
    fun decrypt(ciphertext: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        val expandedKey = keyExpansion(key)
        val plaintext = ByteArray(ciphertext.size)

        var prevBlock = iv.copyOf()
        for (i in ciphertext.indices step 16) {
            val block = ciphertext.copyOfRange(i, i + 16)
            val decrypted = decryptBlock(block, expandedKey)
            for (j in 0 until 16) {
                plaintext[i + j] = (decrypted[j] xor prevBlock[j])
            }
            prevBlock = block
        }

        return pkcs7Unpad(plaintext)
    }

    private fun keyExpansion(key: ByteArray): IntArray {
        val w = IntArray(NB * (NR + 1))

        for (i in 0 until NK) {
            w[i] = (key[4 * i].toInt() and 0xFF shl 24) or
                    (key[4 * i + 1].toInt() and 0xFF shl 16) or
                    (key[4 * i + 2].toInt() and 0xFF shl 8) or
                    (key[4 * i + 3].toInt() and 0xFF)
        }

        for (i in NK until NB * (NR + 1)) {
            var temp = w[i - 1]
            if (i % NK == 0) {
                temp = subWord(rotWord(temp)) xor (RCON[i / NK - 1] shl 24)
            } else if (i % NK == 4) {
                temp = subWord(temp)
            }
            w[i] = w[i - NK] xor temp
        }

        return w
    }

    private fun rotWord(w: Int): Int = (w shl 8) or (w ushr 24)

    private fun subWord(w: Int): Int {
        return (SBOX[(w ushr 24) and 0xFF] shl 24) or
                (SBOX[(w ushr 16) and 0xFF] shl 16) or
                (SBOX[(w ushr 8) and 0xFF] shl 8) or
                (SBOX[w and 0xFF])
    }

    private fun encryptBlock(block: IntArray, expandedKey: IntArray): ByteArray {
        val state = block.copyOf()

        // 初始轮密钥加
        addRoundKey(state, expandedKey, 0)

        // NR-1 轮
        for (round in 1 until NR) {
            subBytes(state)
            shiftRows(state)
            mixColumns(state)
            addRoundKey(state, expandedKey, round)
        }

        // 最后一轮（无 MixColumns）
        subBytes(state)
        shiftRows(state)
        addRoundKey(state, expandedKey, NR)

        return stateToBytes(state)
    }

    private fun decryptBlock(block: ByteArray, expandedKey: IntArray): ByteArray {
        val state = bytesToState(block)

        // 最后一轮逆
        addRoundKey(state, expandedKey, NR)
        invShiftRows(state)
        invSubBytes(state)

        // NR-1 轮逆
        for (round in NR - 1 downTo 1) {
            addRoundKey(state, expandedKey, round)
            invMixColumns(state)
            invShiftRows(state)
            invSubBytes(state)
        }

        // 初始轮密钥加
        addRoundKey(state, expandedKey, 0)

        return stateToBytes(state)
    }

    private fun addRoundKey(state: IntArray, expandedKey: IntArray, round: Int) {
        for (i in 0 until 4) {
            state[i] = state[i] xor expandedKey[round * 4 + i]
        }
    }

    private fun subBytes(state: IntArray) {
        for (i in 0 until 4) {
            state[i] = subWord(state[i])
        }
    }

    private fun invSubBytes(state: IntArray) {
        for (i in 0 until 4) {
            state[i] = (INV_SBOX[(state[i] ushr 24) and 0xFF] shl 24) or
                    (INV_SBOX[(state[i] ushr 16) and 0xFF] shl 16) or
                    (INV_SBOX[(state[i] ushr 8) and 0xFF] shl 8) or
                    (INV_SBOX[state[i] and 0xFF])
        }
    }

    private fun shiftRows(state: IntArray) {
        val bytes = stateToBytes(state)
        // 行移位：第 r 行循环左移 r 字节
        // state 按列存储：bytes[0..3] 第 0 列, bytes[4..7] 第 1 列, ...
        // 第 0 行：bytes[0], bytes[4], bytes[8], bytes[12] — 不移
        // 第 1 行：bytes[1], bytes[5], bytes[9], bytes[13] — 左移 1
        var t = bytes[1]; bytes[1] = bytes[5]; bytes[5] = bytes[9]; bytes[9] = bytes[13]; bytes[13] = t
        // 第 2 行：bytes[2], bytes[6], bytes[10], bytes[14] — 左移 2
        t = bytes[2]; bytes[2] = bytes[10]; bytes[10] = t
        t = bytes[6]; bytes[6] = bytes[14]; bytes[14] = t
        // 第 3 行：bytes[3], bytes[7], bytes[11], bytes[15] — 左移 3
        t = bytes[15]; bytes[15] = bytes[11]; bytes[11] = bytes[7]; bytes[7] = bytes[3]; bytes[3] = t
        for (i in 0 until 4) {
            state[i] = (bytes[4 * i].toInt() and 0xFF shl 24) or
                    (bytes[4 * i + 1].toInt() and 0xFF shl 16) or
                    (bytes[4 * i + 2].toInt() and 0xFF shl 8) or
                    (bytes[4 * i + 3].toInt() and 0xFF)
        }
    }

    private fun invShiftRows(state: IntArray) {
        val bytes = stateToBytes(state)
        // 逆行移位：第 r 行循环右移 r 字节
        // 第 1 行右移 1
        var t = bytes[13]; bytes[13] = bytes[9]; bytes[9] = bytes[5]; bytes[5] = bytes[1]; bytes[1] = t
        // 第 2 行右移 2
        t = bytes[2]; bytes[2] = bytes[10]; bytes[10] = t
        t = bytes[6]; bytes[6] = bytes[14]; bytes[14] = t
        // 第 3 行右移 3
        t = bytes[3]; bytes[3] = bytes[7]; bytes[7] = bytes[11]; bytes[11] = bytes[15]; bytes[15] = t
        for (i in 0 until 4) {
            state[i] = (bytes[4 * i].toInt() and 0xFF shl 24) or
                    (bytes[4 * i + 1].toInt() and 0xFF shl 16) or
                    (bytes[4 * i + 2].toInt() and 0xFF shl 8) or
                    (bytes[4 * i + 3].toInt() and 0xFF)
        }
    }

    private fun mixColumns(state: IntArray) {
        for (i in 0 until 4) {
            val s = state[i]
            val b0 = (s ushr 24) and 0xFF
            val b1 = (s ushr 16) and 0xFF
            val b2 = (s ushr 8) and 0xFF
            val b3 = s and 0xFF
            state[i] = ((gmul(b0, 2) xor gmul(b1, 3) xor b2 xor b3) shl 24) or
                    ((gmul(b1, 2) xor gmul(b2, 3) xor b3 xor b0) shl 16) or
                    ((gmul(b2, 2) xor gmul(b3, 3) xor b0 xor b1) shl 8) or
                    (gmul(b3, 2) xor gmul(b0, 3) xor b1 xor b2)
        }
    }

    private fun invMixColumns(state: IntArray) {
        for (i in 0 until 4) {
            val s = state[i]
            val b0 = (s ushr 24) and 0xFF
            val b1 = (s ushr 16) and 0xFF
            val b2 = (s ushr 8) and 0xFF
            val b3 = s and 0xFF
            state[i] = ((gmul(b0, 14) xor gmul(b1, 11) xor gmul(b2, 13) xor gmul(b3, 9)) shl 24) or
                    ((gmul(b1, 14) xor gmul(b2, 11) xor gmul(b3, 13) xor gmul(b0, 9)) shl 16) or
                    ((gmul(b2, 14) xor gmul(b3, 11) xor gmul(b0, 13) xor gmul(b1, 9)) shl 8) or
                    (gmul(b3, 14) xor gmul(b0, 11) xor gmul(b1, 13) xor gmul(b2, 9))
        }
    }

    private fun gmul(a: Int, b: Int): Int {
        var p = 0
        var aa = a
        var bb = b
        for (i in 0 until 8) {
            if (bb and 1 != 0) p = p xor aa
            val hi = aa and 0x80
            aa = (aa shl 1) and 0xFF
            if (hi != 0) aa = aa xor 0x1b
            bb = bb ushr 1
        }
        return p
    }

    private fun stateToBytes(state: IntArray): ByteArray {
        val result = ByteArray(16)
        for (i in 0 until 4) {
            result[4 * i] = ((state[i] ushr 24) and 0xFF).toByte()
            result[4 * i + 1] = ((state[i] ushr 16) and 0xFF).toByte()
            result[4 * i + 2] = ((state[i] ushr 8) and 0xFF).toByte()
            result[4 * i + 3] = (state[i] and 0xFF).toByte()
        }
        return result
    }

    private fun bytesToState(bytes: ByteArray): IntArray {
        val state = IntArray(4)
        for (i in 0 until 4) {
            state[i] = (bytes[4 * i].toInt() and 0xFF shl 24) or
                    (bytes[4 * i + 1].toInt() and 0xFF shl 16) or
                    (bytes[4 * i + 2].toInt() and 0xFF shl 8) or
                    (bytes[4 * i + 3].toInt() and 0xFF)
        }
        return state
    }

    private fun pkcs7Pad(data: ByteArray, blockSize: Int): ByteArray {
        val padLen = blockSize - (data.size % blockSize)
        val result = ByteArray(data.size + padLen)
        System.arraycopy(data, 0, result, 0, data.size)
        for (i in data.size until result.size) {
            result[i] = padLen.toByte()
        }
        return result
    }

    private fun pkcs7Unpad(data: ByteArray): ByteArray {
        if (data.isEmpty()) return data
        val padLen = data[data.size - 1].toInt() and 0xFF
        if (padLen < 1 || padLen > 16 || padLen > data.size) return data
        // 验证填充
        for (i in data.size - padLen until data.size) {
            if ((data[i].toInt() and 0xFF) != padLen) return data
        }
        return data.copyOfRange(0, data.size - padLen)
    }
}
