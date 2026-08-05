package com.kuikly.init.common.base.platform.toast

import platform.Foundation.NSOperationQueue
import platform.Foundation.NSTimer
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.UIKit.UIViewController

/**
 * iOS Toast 临时实现
 *
 * iOS 无原生 Toast 组件，当前使用 UIAlertController 模拟简单提示。
 * 所有 UI 操作通过 NSOperationQueue.mainQueue 调度到主线程执行。
 * TODO: 替换为自定义 ToastView（Overlay + 自动消失），提升用户体验
 */
actual class Toast {

    actual fun show(message: String, duration: ToastDuration) {
        NSOperationQueue.mainQueue.addOperationWithBlock {
            try {
                val alert = UIAlertController.alertControllerWithTitle(
                    title = null,
                    message = message,
                    preferredStyle = UIAlertControllerStyleAlert
                )
                // 添加一个空操作仅用于占位
                val action = UIAlertAction.actionWithTitle(
                    title = "",
                    style = UIAlertActionStyleCancel,
                    handler = null
                )
                alert.addAction(action)

                val rootVC = getRootViewController()
                rootVC?.presentViewController(alert, animated = true, completion = null)

                // 定时自动消失
                val delaySec = if (duration == ToastDuration.SHORT) 2.0 else 3.5
                NSTimer.scheduledTimerWithTimeInterval(
                    timerInterval = delaySec,
                    repeats = false,
                    block = { timer ->
                        alert.dismissViewControllerAnimated(true, completion = null)
                        timer?.invalidate()
                    }
                )
            } catch (e: Exception) {
                // Toast 显示失败静默处理
            }
        }
    }

    private fun getRootViewController(): UIViewController? {
        val window = UIApplication.sharedApplication.keyWindow
            ?: UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow
        return window?.rootViewController
    }
}

actual fun provideToast(): Toast = Toast()
