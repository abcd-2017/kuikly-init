package com.kuikly.init.common.base.platform.app

/**
 * 应用信息
 *
 * 提供当前应用的元数据。
 *
 * @param appName 应用名称
 * @param packageName 包名 / Bundle ID
 * @param versionName 版本名称（如 "1.0.0"）
 * @param versionCode 版本号（数值）
 * @param buildType 构建类型（debug / release）
 */
data class AppInfo(
    val appName: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val buildType: String
)

/** 全局访问入口 */
expect fun provideAppInfo(): AppInfo
