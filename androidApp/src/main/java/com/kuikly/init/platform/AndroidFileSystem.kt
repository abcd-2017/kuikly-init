package com.kuikly.init.platform

import com.kuikly.init.common.base.platform.FileSystem
import java.io.File

class AndroidFileSystem : FileSystem {
    override fun readFile(path: String): ByteArray = File(path).readBytes()
    override fun writeFile(path: String, data: ByteArray) { File(path).writeBytes(data) }
    override fun exists(path: String): Boolean = File(path).exists()
    override fun delete(path: String): Boolean = File(path).delete()
}
