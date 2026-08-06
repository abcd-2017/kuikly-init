package com.kuikly.init.common.base.platform.picker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import com.kuikly.init.common.base.platform.ActivityResultBridge
import com.kuikly.init.common.base.platform.AppContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Android 文件选择器实现
 *
 * 基于 Intent.ACTION_OPEN_DOCUMENT / ACTION_GET_CONTENT，通过 ActivityResultBridge 获取结果。
 * 支持单选和多选文件、图片、文档。
 *
 * 注意：
 * - 需要宿主 Activity 在 onActivityResult 中调用 ActivityResultBridge.handleResult()。
 * - 返回的路径为 content:// URI，需要通过 ContentResolver 读取。
 */
actual class FilePicker {

    actual suspend fun pickFile(mimeType: String, allowMultiple: Boolean): List<PickedFile> =
        launchPickerIntent(mimeType, allowMultiple)

    actual suspend fun pickImage(allowMultiple: Boolean): List<PickedFile> =
        launchPickerIntent("image/*", allowMultiple)

    actual suspend fun pickDocument(allowMultiple: Boolean): List<PickedFile> =
        launchPickerIntent("application/*", allowMultiple)

    /**
     * 启动文件选择 Intent 并等待结果
     */
    private suspend fun launchPickerIntent(mimeType: String, allowMultiple: Boolean): List<PickedFile> =
        suspendCancellableCoroutine { continuation ->
            try {
                val activity = AppContext.currentActivity
                if (activity == null) {
                    continuation.resume(emptyList())
                    return@suspendCancellableCoroutine
                }

                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = mimeType
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple)
                }

                val requestCode = ActivityResultBridge.register { resultCode, data ->
                    if (continuation.isActive) {
                        if (resultCode == Activity.RESULT_OK) {
                            val files = mutableListOf<PickedFile>()
                            // 处理多选
                            if (allowMultiple) {
                                data?.clipData?.let { clipData ->
                                    for (i in 0 until clipData.itemCount) {
                                        clipData.getItemAt(i).uri?.let { uri ->
                                            files.add(resolveFileInfo(uri))
                                        }
                                    }
                                }
                            }
                            // 处理单选
                            if (files.isEmpty()) {
                                data?.data?.let { uri ->
                                    files.add(resolveFileInfo(uri))
                                }
                            }
                            continuation.resume(files)
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
