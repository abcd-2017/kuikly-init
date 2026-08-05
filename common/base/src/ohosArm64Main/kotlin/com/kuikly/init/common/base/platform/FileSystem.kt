package com.kuikly.init.common.base.platform

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

actual class FileSystem {
    actual fun readFile(path: String): ByteArray =
        getIOHOSPlatformServiceApi()?.readFile(path) ?: ByteArray(0)

    actual fun writeFile(path: String, data: ByteArray) {
        getIOHOSPlatformServiceApi()?.writeFile(path, data)
    }

    actual fun exists(path: String): Boolean =
        getIOHOSPlatformServiceApi()?.fileExists(path) ?: false

    actual fun delete(path: String): Boolean =
        getIOHOSPlatformServiceApi()?.fileDelete(path) ?: false
}
