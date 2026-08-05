package com.kuikly.init.common.base.platform.toast

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

/**
 * OHOS Toast 实现（通过 KNOI 调用 ETS 侧）
 *
 * 基于 @ohos.promptAction，由 ArkTS 侧实现具体逻辑。
 */
actual class Toast {

    private val service get() = getIOHOSPlatformServiceApi()

    actual fun show(message: String, duration: ToastDuration) {
        try {
            val durationCode = if (duration == ToastDuration.SHORT) 0 else 1
            service?.showToast(message, durationCode)
        } catch (e: Exception) {
            // Toast 显示失败静默处理
        }
    }
}

actual fun provideToast(): Toast = Toast()
