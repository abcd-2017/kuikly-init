package com.kuikly.init.common.base.platform.picker

import android.net.Uri
import android.provider.OpenableColumns
import com.kuikly.init.common.base.platform.AppContext

/**
 * Android 文件选择器实现
 *
 * 基于 Intent.ACTION_OPEN_DOCUMENT，通过 suspendCancellableCoroutine 包装回调。
 *
 * 注意：此实现需要在 Activity 上下文中启动 Intent。当前通过 AppContext.application
 * 发送 Intent 会失败（需要 Activity 上下文），因此标记为 TODO。
 *
 * 正确做法：通过 Kuikly 的 Page 生命周期获取当前 Activity，或使用
 * ActivityResultContracts 注册回调。
 */
actual class FilePicker {

    actual suspend fun pickFile(mimeType: String, allowMultiple: Boolean): List<PickedFile> {
        // TODO: 需要 Activity 上下文才能启动 ACTION_OPEN_DOCUMENT
        // 当前 AppContext.application 无法直接 startActivity（FLAG_ACTIVITY_NEW_TASK 也无法返回结果）
        // 后续需通过 Kuikly Page 的 currentActivity 获取 Activity 实例
        return emptyList()
    }

    actual suspend fun pickImage(allowMultiple: Boolean): List<PickedFile> {
        // TODO: 同 pickFile，需要 Activity 上下文
        return emptyList()
    }

    actual suspend fun pickDocument(allowMultiple: Boolean): List<PickedFile> {
        // TODO: 同 pickFile，需要 Activity 上下文
        return emptyList()
    }

    /**
     * 从 URI 解析文件信息
     *
     * 通过 ContentResolver 查询 OpenableColumns 获取文件名和大小。
     */
    private fun resolveFileInfo(uri: Uri): PickedFile {
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

        return PickedFile(
            path = uri.toString(),
            name = name,
            size = size,
            mimeType = mimeType
        )
    }
}

actual fun provideFilePicker(): FilePicker = FilePicker()
