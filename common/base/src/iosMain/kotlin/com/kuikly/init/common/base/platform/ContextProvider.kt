package com.kuikly.init.common.base.platform

import platform.Foundation.NSBundle
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual class ContextProvider {
    actual fun getCacheDirPath(): String {
        val urls = NSFileManager.defaultManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
        return (urls.firstOrNull() as? NSURL)?.path ?: ""
    }
    actual fun getFilesDirPath(): String = getCacheDirPath()
    actual fun readAsset(path: String): ByteArray {
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
