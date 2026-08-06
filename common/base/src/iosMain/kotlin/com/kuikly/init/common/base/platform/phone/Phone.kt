package com.kuikly.init.common.base.platform.phone

import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

/**
 * iOS 电话拨打实现
 *
 * 基于 tel:// URL Scheme 跳转拨号界面，无需权限。
 * 所有 UI 操作通过 NSOperationQueue.mainQueue 调度到主线程执行。
 */
actual class Phone {

    actual fun call(phoneNumber: String) {
        NSOperationQueue.mainQueue.addOperationWithBlock {
            try {
                val url = NSURL.URLWithString("tel://$phoneNumber")
                if (url != null) {
                    UIApplication.sharedApplication.openURL(url)
                }
            } catch (e: Exception) {
                // 跳转失败静默处理
            }
        }
    }
}

actual fun providePhone(): Phone = Phone()
