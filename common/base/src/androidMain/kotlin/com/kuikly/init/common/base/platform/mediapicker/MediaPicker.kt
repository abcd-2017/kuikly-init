package com.kuikly.init.common.base.platform.mediapicker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import com.kuikly.init.common.base.platform.ActivityResultBridge
import com.kuikly.init.common.base.platform.AppContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Android 相册选择器实现
 *
 * 基于 Intent.ACTION_OPEN_DOCUMENT / ACTION_GET_CONTENT，通过 ActivityResultBridge 获取结果。
 * 支持单选/多选图片和视频。
 *
 * 注意：
 * - 需要宿主 Activity 在 onActivityResult 中调用 ActivityResultBridge.handleResult()。
 * - 返回的路径为 content:// URI，需要通过 ContentResolver 读取。
 */
actual class MediaPicker {

    actual suspend fun pickMedia(
        mediaType: MediaMediaType,
        allowMultiple: Boolean,
        callback: (List<PickedMedia>) -> Unit
    ) {
        try {
            val activity = AppContext.currentActivity
            if (activity == null) {
                callback(emptyList())
                return
            }

            val mimeType = when (mediaType) {
                MediaMediaType.IMAGE -> "image/*"
                MediaMediaType.VIDEO -> "video/*"
                MediaMediaType.ALL -> "*/*"
            }

            val result = suspendCancellableCoroutine { continuation ->
                try {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = mimeType
                        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple)
                        // 根据媒体类型设置额外过滤
                        if (mediaType != MediaMediaType.ALL) {
                            putExtra(Intent.EXTRA_MIME_TYPES, when (mediaType) {
                                MediaMediaType.IMAGE -> arrayOf("image/jpeg", "image/png", "image/webp", "image/gif")
                                MediaMediaType.VIDEO -> arrayOf("video/mp4", "video/quicktime", "video/x-msvideo")
                                else -> emptyArray()
                            })
                        }
                    }

                    val requestCode = ActivityResultBridge.register { resultCode, data ->
                        if (continuation.isActive) {
                            if (resultCode == Activity.RESULT_OK) {
                                val media = mutableListOf<PickedMedia>()
                                // 处理多选
                                if (allowMultiple) {
                                    data?.clipData?.let { clipData ->
                                        for (i in 0 until clipData.itemCount) {
                                            clipData.getItemAt(i).uri?.let { uri ->
                                                media.add(resolveMediaInfo(uri))
                                            }
                                        }
                                    }
                                }
                                // 处理单选
                                if (media.isEmpty()) {
                                    data?.data?.let { uri ->
                                        media.add(resolveMediaInfo(uri))
                                    }
                                }
                                continuation.resume(media)
                            } else {
                                // 用户取消选择
                                continuation.resume(emptyList())
                            }
                        }
                    }

                    activity.startActivityForResult(intent, requestCode)
                } catch (e: Exception) {
                    if (continuation.isActive) {
                        continuation.resume(emptyList())
                    }
                }
            }
            callback(result)
        } catch (e: Exception) {
            callback(emptyList())
        }
    }

    /**
     * 从 URI 解析媒体信息
     *
     * 通过 ContentResolver 查询 OpenableColumns 获取文件名和大小。
     */
    private fun resolveMediaInfo(uri: Uri): PickedMedia {
        val context = AppContext.application
        var name = "unknown"
        var size = -1L
        var mimeType: String? = null

        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) name = cursor.getString(nameIndex) ?: "unknown"

                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) {
                        size = cursor.getLong(sizeIndex)
                    }
                }
            }
            mimeType = context.contentResolver.getType(uri)
        } catch (e: Exception) {
            // 查询失败时返回默认值
        }

        return PickedMedia(
            path = uri.toString(),
            name = name,
            size = size,
            mimeType = mimeType ?: "application/octet-stream",
            duration = null
        )
    }
}

actual fun provideMediaPicker(): MediaPicker = MediaPicker()
