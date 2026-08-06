package com.kuikly.init.common.base.platform.dialog

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCoroutine
import kotlin.coroutines.resume
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleActionSheet
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.UIKit.UIViewController

/**
 * iOS 对话框实现
 *
 * 基于 UIAlertController，所有 UI 操作在 NSOperationQueue.mainQueue 中执行。
 * 异步方法通过 callback 返回结果，内部使用 suspendCoroutine 桥接。
 */
actual class Dialog {

    actual fun showAlert(title: String, message: String, confirmText: String) {
        NSOperationQueue.mainQueue.addOperationWithBlock {
            try {
                val alert = UIAlertController.alertControllerWithTitle(
                    title = title,
                    message = message,
                    preferredStyle = UIAlertControllerStyleAlert
                )
                val action = UIAlertAction.actionWithTitle(
                    title = confirmText,
                    style = UIAlertActionStyleDefault,
                    handler = null
                )
                alert.addAction(action)
                getRootViewController()?.presentViewController(alert, animated = true, completion = null)
            } catch (e: Exception) {
                // 对话框显示失败静默处理
            }
        }
    }

    actual fun showConfirm(
        title: String,
        message: String,
        confirmText: String,
        cancelText: String,
        callback: (Int) -> Unit
    ) {
        runBlocking {
            val result = suspendCoroutine { continuation ->
                NSOperationQueue.mainQueue.addOperationWithBlock {
                    try {
                        val alert = UIAlertController.alertControllerWithTitle(
                            title = title,
                            message = message,
                            preferredStyle = UIAlertControllerStyleAlert
                        )
                        val confirmAction = UIAlertAction.actionWithTitle(
                            title = confirmText,
                            style = UIAlertActionStyleDefault,
                            handler = { _ -> continuation.resume(0) }
                        )
                        val cancelAction = UIAlertAction.actionWithTitle(
                            title = cancelText,
                            style = platform.UIKit.UIAlertActionStyleCancel,
                            handler = { _ -> continuation.resume(1) }
                        )
                        alert.addAction(confirmAction)
                        alert.addAction(cancelAction)
                        getRootViewController()?.presentViewController(alert, animated = true, completion = null)
                    } catch (e: Exception) {
                        continuation.resume(-1)
                    }
                }
            }
            callback(result)
        }
    }

    actual fun showActionSheet(
        title: String?,
        message: String?,
        options: List<String>,
        callback: (Int) -> Unit
    ) {
        runBlocking {
            val result = suspendCoroutine { continuation ->
                NSOperationQueue.mainQueue.addOperationWithBlock {
                    try {
                        val alert = UIAlertController.alertControllerWithTitle(
                            title = title,
                            message = message,
                            preferredStyle = UIAlertControllerStyleActionSheet
                        )
                        options.forEachIndexed { index, option ->
                            val action = UIAlertAction.actionWithTitle(
                                title = option,
                                style = UIAlertActionStyleDefault,
                                handler = { _ -> continuation.resume(index) }
                            )
                            alert.addAction(action)
                        }
                        val cancelAction = UIAlertAction.actionWithTitle(
                            title = "取消",
                            style = platform.UIKit.UIAlertActionStyleCancel,
                            handler = { _ -> continuation.resume(-1) }
                        )
                        alert.addAction(cancelAction)
                        getRootViewController()?.presentViewController(alert, animated = true, completion = null)
                    } catch (e: Exception) {
                        continuation.resume(-1)
                    }
                }
            }
            callback(result)
        }
    }

    private fun getRootViewController(): UIViewController? {
        val window = UIApplication.sharedApplication.keyWindow
            ?: UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow
        var rootVC = window?.rootViewController
        while (rootVC?.presentedViewController != null) {
            rootVC = rootVC.presentedViewController
        }
        return rootVC
    }
}

actual fun provideDialog(): Dialog = Dialog()
