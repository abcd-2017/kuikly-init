package com.kuikly.init.common.base.platform.share

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.kuikly.init.common.base.platform.AppContext
import java.io.File

/**
 * Android 系统分享实现
 *
 * 基于 Intent.ACTION_SEND + createChooser。
 * 文件/图片分享通过 FileProvider 获取 content:// URI。
 *
 * 注意：
 * - FileProvider 需要在 AndroidManifest.xml 中配置，并在 res/xml/file_paths.xml 中定义路径。
 * - 若 FileProvider 未配置或文件路径不在声明范围内，会降级为 text/plain 分享。
 */
actual class Share(private val context: Context) {

    actual fun shareText(text: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            launchChooser(intent)
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
            launchChooser(intent)
        } catch (e: Exception) {
            // 分享失败静默处理
        }
    }

    actual fun shareImage(localPath: String) {
        try {
            val file = File(localPath)
            val uri = getFileUri(file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            launchChooser(intent)
        } catch (e: Exception) {
            // 分享失败静默处理
        }
    }

    actual fun shareFile(localPath: String, mimeType: String?) {
        try {
            val file = File(localPath)
            val uri = getFileUri(file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType ?: "*/*"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            launchChooser(intent)
        } catch (e: Exception) {
            // 分享失败静默处理
        }
    }

    /**
     * 启动分享选择器
     */
    private fun launchChooser(intent: Intent) {
        val chooser = Intent.createChooser(intent, null).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    /**
     * 获取文件的 content:// URI
     *
     * 优先使用 FileProvider，失败时降级为 file:// URI。
     */
    private fun getFileUri(file: File): android.net.Uri {
        return try {
            val authority = "${context.packageName}.fileprovider"
            FileProvider.getUriForFile(context, authority, file)
        } catch (e: Exception) {
            // FileProvider 未配置或路径不在声明范围内，降级为 file:// URI
            // 注意：Android 7.0+ 对 file:// URI 有 StrictMode 限制，部分应用可能拒绝接收
            android.net.Uri.fromFile(file)
        }
    }
}

actual fun provideShare(): Share = Share(AppContext.application)
