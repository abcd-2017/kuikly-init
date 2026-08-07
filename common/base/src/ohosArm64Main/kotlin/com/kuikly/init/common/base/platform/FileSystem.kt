package com.kuikly.init.common.base.platform

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

actual class FileSystem {
    actual fun readFile(path: String): ByteArray {
        return try {
            val base64 = getIOHOSPlatformServiceApi()?.readFileBase64(path) ?: ""
            if (base64.isNotEmpty()) decodeBase64(base64) else ByteArray(0)
        } catch (e: Exception) {
            ByteArray(0)
        }
    }

    actual fun writeFile(path: String, data: ByteArray) {
        try {
            val base64 = encodeBase64(data)
            getIOHOSPlatformServiceApi()?.writeFileBase64(path, base64)
        } catch (e: Exception) {
            // 写入失败静默处理
        }
    }

    actual fun exists(path: String): Boolean =
        getIOHOSPlatformServiceApi()?.fileExists(path) ?: false

    actual fun delete(path: String): Boolean =
        getIOHOSPlatformServiceApi()?.fileDelete(path) ?: false

    private fun encodeBase64(data: ByteArray): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
        val sb = StringBuilder()
        var i = 0
        while (i < data.size) {
            val b1 = data[i].toInt() and 0xFF
            val b2 = if (i + 1 < data.size) data[i + 1].toInt() and 0xFF else 0
            val b3 = if (i + 2 < data.size) data[i + 2].toInt() and 0xFF else 0
            sb.append(chars[b1 shr 2])
            sb.append(chars[((b1 and 0x3) shl 4) or (b2 shr 4)])
            sb.append(if (i + 1 < data.size) chars[((b2 and 0xF) shl 2) or (b3 shr 6)] else '=')
            sb.append(if (i + 2 < data.size) chars[b3 and 0x3F] else '=')
            i += 3
        }
        return sb.toString()
    }

    private fun decodeBase64(base64: String): ByteArray {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
        val padded = if (base64.length % 4 != 0) base64 + "=".repeat(4 - base64.length % 4) else base64
        val output = mutableListOf<Byte>()
        var i = 0
        while (i < padded.length) {
            val c1 = chars.indexOf(padded[i])
            val c2 = chars.indexOf(padded[i + 1])
            val c3 = if (i + 2 < padded.length && padded[i + 2] != '=') chars.indexOf(padded[i + 2]) else 0
            val c4 = if (i + 3 < padded.length && padded[i + 3] != '=') chars.indexOf(padded[i + 3]) else 0
            output.add(((c1 shl 2) or (c2 shr 4)).toByte())
            if (i + 2 < padded.length && padded[i + 2] != '=') output.add(((c2 shl 4) or (c3 shr 2)).toByte())
            if (i + 3 < padded.length && padded[i + 3] != '=') output.add(((c3 shl 6) or c4).toByte())
            i += 4
        }
        return output.toByteArray()
    }
}
