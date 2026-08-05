package com.kuikly.init.common.base.platform.clipboard

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

/**
 * OHOS 剪贴板实现（通过 KNOI 调用 ETS 侧）
 *
 * 基于 @ohos.pasteboard，由 ArkTS 侧实现具体逻辑。
 */
actual class Clipboard {

    private val service get() = getIOHOSPlatformServiceApi()

    actual fun copyText(content: String) {
        try {
            service?.setPasteboardText(content)
        } catch (e: Exception) {
            // 写入失败静默处理
        }
    }

    actual fun pasteText(): String {
        return try {
            service?.getPasteboardText() ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    actual fun clear() {
        try {
            service?.clearPasteboard()
        } catch (e: Exception) {
            // 清空失败静默处理
        }
    }

    actual fun hasText(): Boolean {
        return try {
            service?.getPasteboardText()?.isNotEmpty() == true
        } catch (e: Exception) {
            false
        }
    }
}

actual fun provideClipboard(): Clipboard = Clipboard()
