package com.kuikly.init.common.base.platform.dialog

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

/**
 * OHOS 对话框实现（通过 KNOI 调用 ETS 侧）
 *
 * 基于 @ohos.promptAction.showDialog，由 ArkTS 侧实现具体逻辑。
 * 异步方法通过 callback 返回结果。
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

    actual fun showConfirm(
        title: String,
        message: String,
        confirmText: String,
        cancelText: String,
        callback: (Int) -> Unit
    ) {
        try {
            service?.showConfirm(title, message, confirmText, cancelText) { result ->
                callback(result)
            }
        } catch (e: Exception) {
            callback(-1)
        }
    }

    actual fun showActionSheet(
        title: String?,
        message: String?,
        options: List<String>,
        callback: (Int) -> Unit
    ) {
        try {
            service?.showActionSheet(title, message, options) { result ->
                callback(result)
            }
        } catch (e: Exception) {
            callback(-1)
        }
    }
}

actual fun provideDialog(): Dialog = Dialog()
