package com.kuikly.init.common.base.platform

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.create
import platform.Foundation.writeToFile

class IOSFileSystem : FileSystem {
    override fun readFile(path: String): ByteArray {
        val data = NSFileManager.defaultManager.contentsAtPath(path) ?: return ByteArray(0)
        val length = data.length.toInt()
        return ByteArray(length) { i -> data.bytes!![i] }
    }

    override fun writeFile(path: String, data: ByteArray) {
        val nsData = data.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = data.size.toULong())
        }
        nsData.writeToFile(path, atomically = true)
    }

    override fun exists(path: String): Boolean =
        NSFileManager.defaultManager.fileExistsAtPath(path)

    override fun delete(path: String): Boolean =
        NSFileManager.defaultManager.removeItemAtPath(path, null)
}
