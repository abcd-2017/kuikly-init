package com.kuikly.init.platform

import android.app.Application
import com.kuikly.init.common.base.platform.ContextProvider
import java.io.File

class AndroidContextProvider(private val app: Application) : ContextProvider {
    override fun getCacheDirPath(): String = app.cacheDir.absolutePath
    override fun getFilesDirPath(): String = app.filesDir.absolutePath
    override fun readAsset(path: String): ByteArray = app.assets.open(path).readBytes()
}
