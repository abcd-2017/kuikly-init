package com.kuikly.init.common.base.platform.keyboard

import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.UIKit.endEditing

/**
 * iOS 软键盘控制实现
 *
 * hide() 通过 UIApplication.sharedApplication.keyWindow?.endEditing(true) 关闭键盘。
 * show() 需要当前输入框 becomeFirstResponder，此处为空操作（iOS 正常输入框系统自动弹出）。
 * 所有 UI 操作通过 NSOperationQueue.mainQueue 调度到主线程执行。
 */
actual class Keyboard {

    actual fun hide() {
        NSOperationQueue.mainQueue.addOperationWithBlock {
            try {
                val window = UIApplication.sharedApplication.keyWindow
                    ?: UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow
                window?.endEditing(true)
            } catch (e: Exception) {
                // 隐藏失败静默处理
            }
        }
    }

    actual fun show() {
        // iOS 正常输入框系统自动弹出，无需手动触发
        // 如需强制弹出，需当前输入框调用 becomeFirstResponder()
    }
}

actual fun provideKeyboard(): Keyboard = Keyboard()
