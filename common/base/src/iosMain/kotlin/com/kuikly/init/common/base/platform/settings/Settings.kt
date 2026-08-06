package com.kuikly.init.common.base.platform.settings

import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

/**
 * iOS 系统设置实现
 *
 * openSystemSettings() 通过 UIApplication.openSettingsURLString 跳转到应用设置页。
 * iOS 无法直接跳转到系统设置首页，openAppSettings() 与 openSystemSettings() 行为一致。
 */
actual class Settings {
    actual fun openSystemSettings() {
        NSOperationQueue.mainQueue.addOperationWithBlock {
            try {
                val url = NSURL.URLWithString(UIApplication.openSettingsURLString)
                if (url != null) UIApplication.sharedApplication.openURL(url)
            } catch (e: Exception) {
                // 跳转失败静默处理
            }
        }
    }

    actual fun openAppSettings() = openSystemSettings()
}

actual fun provideSettings(): Settings = Settings()
