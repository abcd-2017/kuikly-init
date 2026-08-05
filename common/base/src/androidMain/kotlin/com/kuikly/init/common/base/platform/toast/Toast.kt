package com.kuikly.init.common.base.platform.toast

import android.content.Context
import android.widget.Toast as AndroidToast
import com.kuikly.init.common.base.platform.AppContext

/**
 * Android Toast 实现
 *
 * 基于原生 Toast 组件。
 */
actual class Toast(private val context: Context) {

    actual fun show(message: String, duration: ToastDuration) {
        try {
            val length = if (duration == ToastDuration.SHORT) AndroidToast.LENGTH_SHORT else AndroidToast.LENGTH_LONG
            AndroidToast.makeText(context, message, length).show()
        } catch (e: Exception) {
            // Toast 显示失败静默处理
        }
    }
}

actual fun provideToast(): Toast = Toast(AppContext.application)
