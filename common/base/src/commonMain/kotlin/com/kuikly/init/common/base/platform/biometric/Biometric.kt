package com.kuikly.init.common.base.platform.biometric

/** 生物识别类型 */
enum class BiometricType { FACE, FINGERPRINT, PIN }

/** 生物识别结果 */
enum class BiometricResult { SUCCESS, FAILED, CANCELED, NOT_SUPPORTED }

/**
 * 生物识别能力抽象
 *
 * 提供设备生物识别支持检查、支持的类型查询和认证功能。
 * - Android: 基于 androidx.biometric.BiometricPrompt
 * - iOS: 基于 LocalAuthentication.LAContext
 * - OHOS: 基于 @ohos.userAuth
 */
expect class Biometric {
    /** 检查设备是否支持生物识别 */
    fun isSupported(): Boolean

    /** 获取设备支持的生物识别类型 */
    fun getSupportedTypes(): Set<BiometricType>

    /**
     * 发起生物识别认证
     *
     * @param title 认证对话框标题
     * @param cancelText 取消按钮文案
     * @param callback 回调返回 JSON 字符串，格式：{"result":"SUCCESS"} 或 {"result":"FAILED"} / {"result":"CANCELED"} / {"result":"NOT_SUPPORTED"}
     */
    fun authenticate(title: String, cancelText: String = "取消", callback: (String) -> Unit)
}

/** 全局访问入口 */
expect fun provideBiometric(): Biometric
