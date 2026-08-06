package com.kuikly.init.common.base.platform

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi
import com.tencent.tmm.knoi.type.ArrayBuffer

actual class FileSystem {
    actual fun readFile(path: String): ByteArray {
        return try {
            val arrayBuffer: ArrayBuffer? = getIOHOSPlatformServiceApi()?.readFile(path)
            arrayBuffer?.toByteArray() ?: ByteArray(0)
        } catch (e: Exception) {
            ByteArray(0)
        }
    }

    actual fun writeFile(path: String, data: ByteArray) {
        try {
            val arrayBuffer = ArrayBuffer.fromByteArray(data)
            getIOHOSPlatformServiceApi()?.writeFile(path, arrayBuffer)
        } catch (e: Exception) {
            // 写入失败静默处理
        }
    }

    actual fun exists(path: String): Boolean =
        getIOHOSPlatformServiceApi()?.fileExists(path) ?: false

    actual fun delete(path: String): Boolean =
        getIOHOSPlatformServiceApi()?.fileDelete(path) ?: false
}
