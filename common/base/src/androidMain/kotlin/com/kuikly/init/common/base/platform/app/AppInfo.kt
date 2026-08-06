package com.kuikly.init.common.base.platform.app

import com.kuikly.init.common.base.platform.AppContext

/**
 * Android 应用信息实现
 *
 * 基于 PackageManager 和 BuildConfig。
 */
actual fun provideAppInfo(): AppInfo {
    return try {
        val context = AppContext.application
        val pm = context.packageManager
        val info = pm.getPackageInfo(context.packageName, 0)
        AppInfo(
            appName = context.applicationInfo.loadLabel(pm).toString(),
            packageName = context.packageName,
            versionName = info.versionName ?: "",
            versionCode = info.longVersionCode,
            buildType = if (com.kuikly.init.common.base.BuildConfig.DEBUG) "debug" else "release"
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
