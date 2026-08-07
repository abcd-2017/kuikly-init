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

    /** 读取 assets/rawfile 资源（Base64 编码） */
    fun readAssetBase64(path: String): String

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

    /** 读取文件内容（Base64 编码） */
    fun readFileBase64(path: String): String

    /** 写入文件内容（Base64 编码） */
    fun writeFileBase64(path: String, base64: String)

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

    /** Confirm 确认框：callback 返回 0=确认, 1=取消 */
    fun showConfirm(title: String, message: String, confirmText: String, cancelText: String, callback: (Int) -> Unit)

    /** Action Sheet：多选项，callback 返回用户选择的索引（-1 表示取消） */
    fun showActionSheet(title: String?, message: String?, options: List<String>, callback: (Int) -> Unit)

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

    // ==================== 键盘控制 ====================

    /** 隐藏软键盘 */
    fun hideKeyboard()

    /** 显示软键盘（OHOS 正常输入框系统自动弹出） */
    fun showKeyboard()

    // ==================== 电话拨打 ====================

    /** 拨打电话（跳转到拨号界面） */
    fun callPhone(phoneNumber: String)

    // ==================== 权限请求 ====================

    /**
     * 同步检查权限状态
     *
     * @param tokenID 应用 tokenID（0 表示默认）
     * @param permission 权限名称（如 "ohos.permission.CAMERA"）
     * @return "GRANTED" / "DENIED" / "NOT_DETERMINED"
     */
    fun checkPermissionSync(tokenID: Long, permission: String): String

    /**
     * 异步申请权限
     *
     * @param permissions 权限列表
     * @param callback 回调返回 "GRANTED" / "DENIED"
     */
    fun requestPermissions(permissions: List<String>, callback: (String) -> Unit)

    // ==================== 屏幕信息 ====================

    /** 屏幕宽度（像素） */
    fun getScreenWidth(): Int

    /** 屏幕高度（像素） */
    fun getScreenHeight(): Int

    /** 屏幕 DPI */
    fun getScreenDensityDpi(): Int

    /** 屏幕密度比例 */
    fun getScreenDensity(): Double

    /** 屏幕旋转方向（0/90/180/270） */
    fun getScreenRotation(): Int

    // ==================== 应用信息 ====================

    /** 应用名称 */
    fun getAppName(): String

    /** 包名 */
    fun getAppPackageName(): String

    /** 版本名称 */
    fun getVersionName(): String

    /** 版本号 */
    fun getVersionCode(): Long

    /** 构建类型（debug / release） */
    fun getBuildType(): String

    // ==================== 生物识别 ====================

    /** 检查设备是否支持生物识别 */
    fun isBiometricSupported(): Boolean

    /** 获取支持的生物识别类型（JSON 数组，如 ["FACE","FINGERPRINT"]） */
    fun getSupportedBiometricTypes(): String

    /**
     * 发起生物识别认证
     *
     * @param title 认证对话框标题
     * @param cancelText 取消按钮文案
     * @param callback 回调返回 JSON 字符串，格式：{"result":"SUCCESS"} 或 {"result":"FAILED"} / {"result":"CANCELED"} / {"result":"NOT_SUPPORTED"}
     */
    fun authenticate(title: String, cancelText: String, callback: (String) -> Unit)

    // ==================== 震动反馈 ====================

    /**
     * 冲击反馈
     *
     * @param style 0=HEAVY, 1=MEDIUM, 2=LIGHT
     */
    fun hapticImpact(style: Int)

    /**
     * 通知反馈
     *
     * @param type 0=SUCCESS, 1=WARNING, 2=FAILURE
     */
    fun hapticNotification(type: Int)

    /** 选择变更反馈（轻点） */
    fun hapticSelectionChanged()

    /** 停止当前震动 */
    fun hapticStop()

    // ==================== 地理位置 ====================

    /** 请求定位权限（返回是否授权） */
    fun requestLocationPermission(): Boolean

    /**
     * 获取当前位置
     *
     * @param accuracy 精度等级（0=COARSE, 1=BALANCED, 2=PRECISE）
     * @param callback 回调返回 JSON 字符串，格式：{"latitude":31.23,"longitude":121.47,...}
     */
    fun getCurrentLocation(accuracy: Int, callback: (String) -> Unit)

    // ==================== 扫码 ====================

    /**
     * 启动扫码
     *
     * @param callback 回调返回 JSON 字符串，格式：{"content":"...","format":"QR_CODE"}
     */
    fun startScan(callback: (String) -> Unit)

    /** 停止扫码 */
    fun stopScan()

    // ==================== 电池状态 ====================

    /** 电量百分比（0-100），未知时为 -1 */
    fun getBatteryLevel(): Int

    /** 是否正在充电 */
    fun isCharging(): Boolean

    /** 是否低电量（电量 <= 20%） */
    fun isLowBattery(): Boolean

    // ==================== 系统设置 ====================

    /** 打开系统设置首页 */
    fun openSystemSettings()

    /** 打开当前应用的设置详情页 */
    fun openAppSettings()

    // ==================== 相机 ====================

    /**
     * 拍照
     * @param callback 回调返回 JSON 字符串：{"path":"...","name":"...","size":123,"mimeType":"image/jpeg"}
     */
    fun capturePhoto(callback: (String) -> Unit)

    /**
     * 录视频
     * @param callback 回调返回 JSON 字符串
     */
    fun recordVideo(callback: (String) -> Unit)

    // ==================== 媒体选择器 ====================

    /**
     * 选择媒体文件
     * @param mediaType 0=图片, 1=视频, 2=全部
     * @param allowMultiple 是否允许多选
     * @param callback 回调返回 JSON 字符串：{"files":[...]}
     */
    fun pickMedia(mediaType: Int, allowMultiple: Boolean, callback: (String) -> Unit)
}
