package com.kuikly.init.common.base.platform.app

import platform.Foundation.NSBundle

/**
 * iOS 应用信息实现
 *
 * 基于 NSBundle.mainBundle 获取应用元数据。
 */
actual fun provideAppInfo(): AppInfo {
    return try {
        val bundle = NSBundle.mainBundle
        val infoDict = bundle.infoDictionary
        AppInfo(
            appName = infoDict?.get("CFBundleDisplayName") as? String
                ?: infoDict?.get("CFBundleName") as? String
                ?: "",
            packageName = bundle.bundleIdentifier ?: "",
            versionName = infoDict?.get("CFBundleShortVersionString") as? String ?: "",
            versionCode = (infoDict?.get("CFBundleVersion") as? String)?.toLongOrNull() ?: 0L,
            buildType = if (isDebugBuild()) "debug" else "release"
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

/**
 * 判断当前是否为 Debug 构建
 *
 * iOS 无直接 BuildConfig，通过 embedded mobile provisioning 存在性判断：
 * - Development 签名包含 embedded.mobileprovision → debug
 * - Distribution 签名不包含 → release
 */
private fun isDebugBuild(): Boolean {
    return try {
        val profilePath = NSBundle.mainBundle.pathForResource("embedded", ofType: "mobileprovision")
        profilePath != null
    } catch (e: Exception) {
        false
    }
}
