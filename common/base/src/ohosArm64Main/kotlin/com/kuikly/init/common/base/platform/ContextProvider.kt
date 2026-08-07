package com.kuikly.init.common.base.platform

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

actual class ContextProvider {
    actual fun getCacheDirPath(): String =
        getIOHOSPlatformServiceApi()?.getCacheDirPath() ?: "/data/storage/el2/base/haps/entry/cache"

    actual fun getFilesDirPath(): String =
        getIOHOSPlatformServiceApi()?.getFilesDirPath() ?: "/data/storage/el2/base/haps/entry/files"

    actual fun readAsset(path: String): ByteArray {
        return try {
            val base64 = getIOHOSPlatformServiceApi()?.readAssetBase64(path)
            if (!base64.isNullOrEmpty()) {
                decodeBase64(base64)
            } else {
                ByteArray(0)
            }
        } catch (e: Exception) {
            ByteArray(0)
        }
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
