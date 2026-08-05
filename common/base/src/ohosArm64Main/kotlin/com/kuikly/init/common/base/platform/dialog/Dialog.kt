package com.kuikly.init.common.base.platform.dialog

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

/**
 * OHOS 对话框实现（通过 KNOI 调用 ETS 侧）
 *
 * 基于 @ohos.promptAction.showDialog，由 ArkTS 侧实现具体逻辑。
 *
 * 限制说明：
 * - promptAction.showDialog 返回 Promise（异步），但 KNOI 调用为同步机制。 * - showAlert 无返回值，可直接使用。
 * - showConfirm / showActionSheet 依赖用户点击回调，同步调用无法等待 Promise 结果。
 *   当前实现会弹出对话框，但返回值为占位（false / -1），无法反映用户真实选择。
 * - TODO: 需改用 KNOI 异步回调机制或原生端事件推送，才能获取用户实际选择。
 */
actual class Dialog {

    private val service get() = getIOHOSPlatformServiceApi()

    actual fun showAlert(title: String, message: String, confirmText: String) {
        try {
            service?.showAlert(title, message, confirmText)
        } catch (e: Exception) {
            // 对话框显示失败静默处理
        }
    }

    actual suspend fun showConfirm(
        title: String,
        message: String,
        confirmText: String,
        cancelText: String
    ): Boolean {
        // OHOS 限制：同步 KNOI 调用无法等待 Promise 结果，返回占位值
        try {
            service?.showConfirm(title, message, confirmText, cancelText)
        } catch (e: Exception) {
            // 静默处理
        }
        return false // 占位：无法获取用户真实选择
    }

    actual suspend fun showActionSheet(
        title: String?,
        message: String?,
        options: List<String>
    ): Int {
        // OHOS 限制：同步 KNOI 调用无法等待 Promise 结果，返回占位值
        try {
            service?.showActionSheet(title, message, options)
        } catch (e: Exception) {
            // 静默处理
        }
        return -1 // 占位：无法获取用户真实选择
    }
}

actual fun provideDialog(): Dialog = Dialog()
