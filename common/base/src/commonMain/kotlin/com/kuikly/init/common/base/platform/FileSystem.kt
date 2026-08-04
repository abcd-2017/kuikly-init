package com.kuikly.init.common.base.platform

/**
 * 文件系统抽象
 */
interface FileSystem {

    /** 读取文件内容 */
    fun readFile(path: String): ByteArray

    /** 写入文件内容 */
    fun writeFile(path: String, data: ByteArray)

    /** 判断文件是否存在 */
    fun exists(path: String): Boolean

    /** 删除文件 */
    fun delete(path: String): Boolean
}
