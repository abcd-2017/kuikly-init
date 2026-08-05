package com.kuikly.init.common.base.platform.share

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

/**
 * OHOS 系统分享实现（通过 KNOI 调用 ETS 侧）
 *
 * 基于 @ohos.share systemShare.getShareController().showShare(ShareData)，
 * 由 ArkTS 侧实现具体逻辑。
 */
actual class Share {

    private val service get() = getIOHOSPlatformServiceApi()

    actual fun shareText(text: String) {
        try {
            service?.shareText(text)
        } catch (e: Exception) {
            // 分享失败静默处理
        }
    }

    actual fun shareLink(url: String, title: String?, description: String?) {
        try {
            service?.shareLink(url, title, description)
        } catch (e: Exception) {
            // 分享失败静默处理
        }
    }

    actual fun shareImage(localPath: String) {
        try {
            service?.shareImage(localPath)
        } catch (e: Exception) {
            // 分享失败静默处理
        }
    }

    actual fun shareFile(localPath: String, mimeType: String?) {
        try {
            service?.shareFile(localPath, mimeType)
        } catch (e: Exception) {
            // 分享失败静默处理
        }
    }
}

actual fun provideShare(): Share = Share()
