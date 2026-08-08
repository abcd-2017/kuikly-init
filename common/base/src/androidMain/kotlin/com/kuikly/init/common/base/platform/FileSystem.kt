package com.kuikly.init.common.base.platform

import java.io.File

actual class FileSystem {
    actual fun readFile(path: String): ByteArray = File(path).readBytes()
    actual fun writeFile(path: String, data: ByteArray) { File(path).writeBytes(data) }
    actual fun exists(path: String): Boolean = File(path).exists()
    actual fun delete(path: String): Boolean = File(path).delete()
}

actual fun provideFileSystem(): FileSystem = FileSystem()
