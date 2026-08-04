package com.kuikly.init.common.base.platform

/**
 * 平台上下文抽象
 *
 * 提供应用级资源访问能力，各平台实现封装原生上下文。
 */
interface ContextProvider {

    /** 获取缓存目录路径 */
    fun getCacheDirPath(): String

    /** 获取文件存储目录路径 */
    fun getFilesDirPath(): String

    /** 读取 assets/rawfile 资源 */
    fun readAsset(path: String): ByteArray
}
