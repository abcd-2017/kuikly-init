package com.kuikly.init.common.base.platform.settings

/**
 * 系统设置能力抽象
 *
 * 提供跳转到系统设置和应用设置页面的功能。
 */
expect class Settings {
    /** 打开系统设置首页 */
    fun openSystemSettings()

    /** 打开当前应用的设置详情页 */
    fun openAppSettings()
}

/** 全局访问入口 */
expect fun provideSettings(): Settings
