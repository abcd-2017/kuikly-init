package com.kuikly.init.common.base.platform.share

import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.popoverPresentationController

/**
 * iOS 系统分享实现
 *
 * 基于 UIActivityViewController。
 * iPad 需处理 popoverPresentationController 避免崩溃。
 * 所有 UI 操作通过 NSOperationQueue.mainQueue 调度到主线程执行。
 */
actual class Share {

    actual fun shareText(text: String) {
        shareContent(text)
    }

    actual fun shareLink(url: String, title: String?, description: String?) {
        // iOS 分享链接时直接传 URL 文本，系统会自动识别
        val shareText = buildString {
            append(url)
            if (!title.isNullOrEmpty()) {
                append("\n\n")
                append(title)
            }
            if (!description.isNullOrEmpty()) {
                append("\n")
                append(description)
            }
        }
        shareContent(shareText)
    }

    actual fun shareImage(localPath: String) {
        // 将本地路径转为 NSURL，UIActivityViewController 会自动识别图片类型
        val url = NSURL.fileURLWithPath(localPath)
        shareContent(url)
    }

    actual fun shareFile(localPath: String, mimeType: String?) {
        // 将本地路径转为 NSURL，UIActivityViewController 会根据扩展名推断类型
        val url = NSURL.fileURLWithPath(localPath)
        shareContent(url)
    }

    private fun shareContent(item: Any) {
        NSOperationQueue.mainQueue.addOperationWithBlock {
            try {
                val rootVC = getRootViewController()
                if (rootVC == null) {
                    return@addOperationWithBlock
                }

                val activityVC = UIActivityViewController.alloc()?.initWithActivityItems(
                    applicationActivities = null,
                    activityItems = listOf(item)
                ) ?: return@addOperationWithBlock

                // iPad 适配：必须设置 popoverPresentationController 的 sourceView
                activityVC.popoverPresentationController?.let { popover ->
                    popover.sourceView = rootVC.view
                    popover.sourceRect = platform.CoreGraphics.CGRectMake(
                        rootVC.view.bounds.useContents.size.width / 2,
                        rootVC.view.bounds.useContents.size.height / 2,
                        0.0,
                        0.0
                    )
                    popover.permittedArrowDirections = 0u
                }

                rootVC.presentViewController(activityVC, animated = true, completion = null)
            } catch (e: Exception) {
                // 分享失败静默处理
            }
        }
    }

    private fun getRootViewController(): UIViewController? {
        val window = UIApplication.sharedApplication.keyWindow
            ?: UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow
        var vc = window?.rootViewController
        while (vc?.presentedViewController != null) {
            vc = vc.presentedViewController
        }
        return vc
    }
}

actual fun provideShare(): Share = Share()
