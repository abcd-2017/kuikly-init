package com.kuikly.init.common.base

import com.tencent.tmm.knoi.annotation.ServiceConsumer
import com.tencent.tmm.knoi.type.ArrayBuffer

/**
 * OHOS 平台能力服务接口（KNOI）
 *
 * 通过 KNOI 机制实现 Kotlin ↔ ArkTS 互调，无需 C 桥接代码。
 * 实现由 ArkTS 侧通过 registerServiceProvider 注册。
 */
@ServiceConsumer
interface IOHOSPlatformService {
    /** 获取缓存目录路径 */
    fun getCacheDirPath(): String

    /** 获取文件存储目录路径 */
    fun getFilesDirPath(): String

    /** 读取 assets/rawfile 资源 */
    fun readAsset(path: String): ArrayBuffer

    /** 设备唯一标识 */
    fun getDeviceId(): String

    /** 操作系统版本 */
    fun getOSVersion(): String

    /** 设备型号 */
    fun getDeviceModel(): String

    /** 读取文件内容 */
    fun readFile(path: String): ArrayBuffer

    /** 写入文件内容 */
    fun writeFile(path: String, data: ArrayBuffer)

    /** 判断文件是否存在 */
    fun fileExists(path: String): Boolean

    /** 删除文件 */
    fun fileDelete(path: String): Boolean

    /** 当前是否联网 */
    fun isNetworkConnected(): Boolean

    /** 当前网络类型：wifi / cellular / none */
    fun getNetworkType(): String
}
