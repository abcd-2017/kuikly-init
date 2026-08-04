package com.kuikly.init.common.base.platform

import android.app.Application
import java.io.File

/**
 * Android 平台 ContextProvider 实现
 */
class AndroidContextProvider(private val app: Application) : ContextProvider {
    override fun getCacheDirPath(): String = app.cacheDir.absolutePath
    override fun getFilesDirPath(): String = app.filesDir.absolutePath
    override fun readAsset(path: String): ByteArray = app.assets.open(path).readBytes()
}
