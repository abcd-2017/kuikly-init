package com.kuikly.init.common.base.platform.clipboard

import platform.UIKit.UIPasteboard

/**
 * iOS 剪贴板实现
 *
 * 基于 UIPasteboard。
 */
actual class Clipboard {

    actual fun copyText(content: String) {
        try {
            UIPasteboard.generalPasteboard.string = content
        } catch (e: Exception) {
            // 写入失败静默处理
        }
    }

    actual fun pasteText(): String {
        return try {
            UIPasteboard.generalPasteboard.string ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    actual fun clear() {
        try {
            UIPasteboard.generalPasteboard.string = ""
        } catch (e: Exception) {
            // 清空失败静默处理
        }
    }

    actual fun hasText(): Boolean {
        return try {
            UIPasteboard.generalPasteboard.string?.isNotEmpty() == true
        } catch (e: Exception) {
            false
        }
    }
}

actual fun provideClipboard(): Clipboard = Clipboard()
