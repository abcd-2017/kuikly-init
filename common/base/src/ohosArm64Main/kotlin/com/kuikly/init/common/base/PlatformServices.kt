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

    // ==================== 时区 ====================

    /** 时区 ID，如 "Asia/Shanghai" */
    fun getTimezoneId(): String

    /** 时区偏移量（分钟），如东八区返回 480 */
    fun getOffsetMinutes(): Int

    /** 当前是否处于夏令时 */
    fun isDaylightSaving(): Boolean

    /** 时区缩写，如 "CST" */
    fun getTimezoneAbbreviation(): String

    // ==================== NTP ====================

    /** 获取服务器时间戳（毫秒），失败返回 -1 */
    fun getServerTime(ntpServer: String): Long

    /** 获取本地时间与服务器时间的偏差（毫秒），失败返回 0 */
    fun getClockOffset(ntpServer: String): Long

    // ==================== 加解密 ====================

    /** AES-256-CBC 加密，返回 Base64 编码密文 */
    fun aesEncrypt(plaintext: String, key: String, iv: String?): String

    /** AES-256-CBC 解密 */
    fun aesDecrypt(ciphertext: String, key: String, iv: String?): String

    /** MD5 哈希（32 位小写 hex） */
    fun md5(input: String): String

    /** SHA-256 哈希（64 位小写 hex） */
    fun sha256(input: String): String

    /** HMAC-SHA256（Base64 编码） */
    fun hmacSha256(data: String, key: String): String

    /** Base64 编码 */
    fun base64Encode(input: String): String

    /** Base64 解码 */
    fun base64Decode(input: String): String

    // ==================== 剪贴板 ====================

    /** 写入文本到剪贴板 */
    fun setPasteboardText(content: String)

    /** 读取剪贴板文本 */
    fun getPasteboardText(): String

    /** 清空剪贴板 */
    fun clearPasteboard()

    // ==================== Toast ====================

    /** 显示 Toast 提示（duration: 0=short, 1=long） */
    fun showToast(message: String, duration: Int)

    // ==================== 系统分享 ====================

    /** 分享文本 */
    fun shareText(text: String)

    /** 分享链接 */
    fun shareLink(url: String, title: String?, description: String?)

    /** 分享图片（本地路径） */
    fun shareImage(localPath: String)

    /** 分享文件（本地路径 + MIME 类型） */
    fun shareFile(localPath: String, mimeType: String?)

    // ==================== 对话框 ====================

    /** Alert 提示框：标题 + 消息 + 确认按钮 */
    fun showAlert(title: String, message: String, confirmText: String)

    /** Confirm 确认框：返回 0=确认, 1=取消 */
    fun showConfirm(title: String, message: String, confirmText: String, cancelText: String): Int

    /** Action Sheet：多选项，返回用户选择的索引（-1 表示取消） */
    fun showActionSheet(title: String?, message: String?, options: List<String>): Int

    // ==================== 文件选择器 ====================

    /**
     * 选择文件
     *
     * @param mimeType 过滤的 MIME 类型
     * @param allowMultiple 是否允许多选
     * @return JSON 字符串，格式：{"files":[{"path":"...","name":"...","size":123,"mimeType":"..."}]}
     */
    fun pickFile(mimeType: String, allowMultiple: Boolean): String

    /**
     * 选择图片
     *
     * @param allowMultiple 是否允许多选
     * @return JSON 字符串
     */
    fun pickImage(allowMultiple: Boolean): String

    /**
     * 选择文档
     *
     * @param allowMultiple 是否允许多选
     * @return JSON 字符串
     */
    fun pickDocument(allowMultiple: Boolean): String
}
