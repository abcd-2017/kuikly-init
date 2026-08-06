package com.kuikly.init.common.base.platform.settings

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

/**
 * OHOS 系统设置实现（通过 KNOI 调用 ETS 侧）
 *
 * 跳转到系统设置/应用设置页面，由 ArkTS 侧实现具体逻辑。
 */
actual class Settings {
    private val service get() = getIOHOSPlatformServiceApi()

    actual fun openSystemSettings() {
        try {
            service?.openSystemSettings()
        } catch (e: Exception) {
            // 跳转失败静默处理
        }
    }

    actual fun openAppSettings() {
        try {
            service?.openAppSettings()
        } catch (e: Exception) {
            // 跳转失败静默处理
        }
    }
}

actual fun provideSettings(): Settings = Settings()
