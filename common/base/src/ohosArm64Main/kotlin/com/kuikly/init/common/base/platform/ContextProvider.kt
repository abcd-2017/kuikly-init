package com.kuikly.init.common.base.platform

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

actual class ContextProvider {
    actual fun getCacheDirPath(): String =
        getIOHOSPlatformServiceApi()?.getCacheDirPath() ?: "/data/storage/el2/base/haps/entry/cache"

    actual fun getFilesDirPath(): String =
        getIOHOSPlatformServiceApi()?.getFilesDirPath() ?: "/data/storage/el2/base/haps/entry/files"

    actual fun readAsset(path: String): ByteArray {
        return try {
            val arrayBuffer = getIOHOSPlatformServiceApi()?.readAsset(path)
            arrayBuffer?.toByteArray() ?: ByteArray(0)
        } catch (e: Exception) {
            ByteArray(0)
        }
    }
}
