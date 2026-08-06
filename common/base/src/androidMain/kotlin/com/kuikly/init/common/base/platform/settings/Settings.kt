package com.kuikly.init.common.base.platform.settings

import android.content.Intent
import android.net.Uri
import com.kuikly.init.common.base.platform.AppContext

/**
 * Android 系统设置实现
 *
 * 基于 Intent 跳转系统设置和应用详情页。
 */
actual class Settings {
    actual fun openSystemSettings() {
        try {
            val intent = Intent(android.provider.Settings.ACTION_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            AppContext.application.startActivity(intent)
        } catch (e: Exception) {
            // 跳转失败静默处理
        }
    }

    actual fun openAppSettings() {
        try {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:${AppContext.application.packageName}")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            AppContext.application.startActivity(intent)
        } catch (e: Exception) {
            // 跳转失败静默处理
        }
    }
}

actual fun provideSettings(): Settings = Settings()
