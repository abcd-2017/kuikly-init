package com.kuikly.init.common.base.platform

import java.io.File

/**
 * Android 平台 FileSystem 实现
 */
class AndroidFileSystem : FileSystem {
    override fun readFile(path: String): ByteArray = File(path).readBytes()
    override fun writeFile(path: String, data: ByteArray) { File(path).writeBytes(data) }
    override fun exists(path: String): Boolean = File(path).exists()
    override fun delete(path: String): Boolean = File(path).delete()
}
