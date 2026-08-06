package com.kuikly.init.common.base.platform.keyboard

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

/**
 * OHOS 软键盘控制实现（通过 KNOI 调用 ETS 侧）
 *
 * 基于 @kit.InputMethodKit inputMethod.getController().hideTextInput()，
 * 由 ArkTS 侧实现具体逻辑。
 */
actual class Keyboard {

    private val service get() = getIOHOSPlatformServiceApi()

    actual fun hide() {
        try {
            service?.hideKeyboard()
        } catch (e: Exception) {
            // 隐藏失败静默处理
        }
    }

    actual fun show() {
        try {
            service?.showKeyboard()
        } catch (e: Exception) {
            // 显示失败静默处理
        }
    }
}

actual fun provideKeyboard(): Keyboard = Keyboard()
