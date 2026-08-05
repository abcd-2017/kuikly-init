package com.kuikly.init.common.base.platform.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import com.kuikly.init.common.base.platform.AppContext

/**
 * Android 剪贴板实现
 *
 * 基于 ClipboardManager。
 * 注意：Android 10+ 后台应用无法读取剪贴板，pasteText() 会兜底返回空字符串。
 */
actual class Clipboard(private val context: Context) {

    private val cm: ClipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    actual fun copyText(content: String) {
        try {
            cm.setPrimaryClip(ClipData.newPlainText("kuikly", content))
        } catch (e: Exception) {
            // 写入失败静默处理
        }
    }

    actual fun pasteText(): String {
        return try {
            cm.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
        } catch (e: Exception) {
            // Android 10+ 后台读取会抛异常，兜底返回空字符串
            ""
        }
    }

    actual fun clear() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                cm.clearPrimaryClip()
            } else {
                @Suppress("DEPRECATION")
                cm.setPrimaryClip(ClipData.newPlainText("", ""))
            }
        } catch (e: Exception) {
            // 清空失败静默处理
        }
    }

    actual fun hasText(): Boolean {
        return try {
            cm.primaryClip?.getItemAt(0)?.text != null
        } catch (e: Exception) {
            false
        }
    }
}

actual fun provideClipboard(): Clipboard = Clipboard(AppContext.application)
