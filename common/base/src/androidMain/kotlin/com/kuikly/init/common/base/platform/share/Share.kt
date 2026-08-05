package com.kuikly.init.common.base.platform.share

import android.content.Intent
import com.kuikly.init.common.base.platform.AppContext

/**
 * Android 系统分享实现
 *
 * 基于 Intent.ACTION_SEND + createChooser。
 * 文件/图片分享因 FileProvider 配置复杂，当前标注 TODO。
 */
actual class Share(private val context: android.content.Context) {

    actual fun shareText(text: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            val chooser = Intent.createChooser(intent, null).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            // 分享失败静默处理
        }
    }

    actual fun shareLink(url: String, title: String?, description: String?) {
        try {
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
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            val chooser = Intent.createChooser(intent, null).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            // 分享失败静默处理
        }
    }

    actual fun shareImage(localPath: String) {
        // TODO: 需配置 FileProvider（AndroidManifest + file_paths.xml）
        // 实现后通过 Uri.fromFile + FileProvider.getUriForFile 获取 content:// URI
        // 此处暂不支持，避免崩溃
    }

    actual fun shareFile(localPath: String, mimeType: String?) {
        // TODO: 同 shareImage，需 FileProvider 支持
    }
}

actual fun provideShare(): Share = Share(AppContext.application)
