package com.kuikly.init.common.base.platform.toast

/** Toast 显示时长 */
enum class ToastDuration { SHORT, LONG }

/**
 * Toast 提示能力抽象
 *
 * 提供轻量级文本提示功能。iOS 无原生 Toast，具体实现可能为自定义 View 或 Alert。
 */
expect class Toast {
    /** 显示 Toast 提示 */
    fun show(message: String, duration: ToastDuration = ToastDuration.SHORT)
}

/** 全局访问入口 */
expect fun provideToast(): Toast
