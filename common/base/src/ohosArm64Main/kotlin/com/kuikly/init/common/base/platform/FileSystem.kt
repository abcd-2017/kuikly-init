package com.kuikly.init.common.base.platform

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

actual class FileSystem {
    actual fun readFile(path: String): ByteArray {
        // TODO: Convert ArrayBuffer to ByteArray
        getIOHOSPlatformServiceApi()?.readFile(path)
        return ByteArray(0)
    }

    actual fun writeFile(path: String, data: ByteArray) {
        // TODO: Convert ByteArray to ArrayBuffer
    }

    actual fun exists(path: String): Boolean =
        getIOHOSPlatformServiceApi()?.fileExists(path) ?: false

    actual fun delete(path: String): Boolean =
        getIOHOSPlatformServiceApi()?.fileDelete(path) ?: false
}
