package com.kuikly.init.common.base.platform

import android.app.Application

actual class ContextProvider(private val app: Application) {
    actual fun getCacheDirPath(): String = app.cacheDir.absolutePath
    actual fun getFilesDirPath(): String = app.filesDir.absolutePath
    actual fun readAsset(path: String): ByteArray = app.assets.open(path).readBytes()
}
