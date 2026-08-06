package com.kuikly.init.common.base.platform.app

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

/**
 * OHOS 应用信息实现（通过 KNOI 调用 ETS 侧）
 *
 * 基于 bundleManager，由 ArkTS 侧实现具体逻辑。
 */
actual fun provideAppInfo(): AppInfo {
    return try {
        val service = getIOHOSPlatformServiceApi()
        AppInfo(
            appName = service?.getAppName() ?: "",
            packageName = service?.getAppPackageName() ?: "",
            versionName = service?.getVersionName() ?: "",
            versionCode = service?.getVersionCode() ?: 0L,
            buildType = service?.getBuildType() ?: "unknown"
        )
    } catch (e: Exception) {
        AppInfo(
            appName = "",
            packageName = "",
            versionName = "",
            versionCode = 0L,
            buildType = "unknown"
        )
    }
}
