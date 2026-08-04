package com.kuikly.init.common.base.platform

import platform.Foundation.NSBundle
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

class IOSContextProvider : ContextProvider {
    override fun getCacheDirPath(): String {
        val urls = NSFileManager.defaultManager.URLsForDirectory(
            NSDocumentDirectory,
            NSUserDomainMask
        )
        return (urls.firstOrNull() as? NSURL)?.path ?: ""
    }

    override fun getFilesDirPath(): String = getCacheDirPath()

    override fun readAsset(path: String): ByteArray {
        // iOS 资源从 Bundle 读取
        val dotIndex = path.lastIndexOf('.')
        if (dotIndex < 0) return ByteArray(0)
        val name = path.substring(0, dotIndex)
        val ext = path.substring(dotIndex + 1)
        val assetPath = NSBundle.mainBundle.pathForResource(name, ext) ?: return ByteArray(0)
        val data = NSFileManager.defaultManager.contentsAtPath(assetPath) ?: return ByteArray(0)
        val length = data.length.toInt()
        return ByteArray(length) { i -> data.bytes!![i] }
    }
}
