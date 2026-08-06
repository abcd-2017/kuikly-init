package com.kuikly.init.common.base.platform.camera

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.kuikly.init.common.base.platform.ActivityResultBridge
import com.kuikly.init.common.base.platform.AppContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import java.io.File

/**
 * Android 相机实现
 *
 * 基于 MediaStore.ACTION_IMAGE_CAPTURE / ACTION_VIDEO_CAPTURE，
 * 通过 ActivityResultBridge 获取结果。
 *
 * 注意：
 * - 拍照/录像需要宿主 Activity 在 onActivityResult 中调用 ActivityResultBridge.handleResult()。
 * - 拍照输出使用 FileProvider 生成 content:// URI，需要 AndroidManifest 中配置 FileProvider。
 * - 返回的路径为 content:// URI，需要通过 ContentResolver 读取。
 */
actual class Camera {

    /** 当前拍照输出的 URI（用于拍照 Intent 的 EXTRA_OUTPUT） */
    private var currentPhotoUri: Uri? = null

    actual suspend fun capturePhoto(callback: (CapturedMedia?) -> Unit) {
        try {
            val activity = AppContext.currentActivity
            if (activity == null) {
                callback(null)
                return
            }

            val result = suspendCancellableCoroutine { continuation ->
                try {
                    // 创建输出文件 URI
                    val photoFile = createTempImageFile()
                    val photoUri = getFileUri(photoFile)
                    currentPhotoUri = photoUri

                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                        putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                        addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    }

                    val requestCode = ActivityResultBridge.register { resultCode, _ ->
                        if (continuation.isActive) {
                            if (resultCode == Activity.RESULT_OK) {
                                val media = resolveCapturedMedia(photoUri, photoFile)
                                continuation.resume(media)
                            } else {
                                // 用户取消
                                continuation.resume(null)
                            }
                        }
                    }

                    activity.startActivityForResult(intent, requestCode)
                } catch (e: Exception) {
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }
            }
            callback(result)
        } catch (e: Exception) {
            callback(null)
        }
    }

    actual suspend fun recordVideo(callback: (CapturedMedia?) -> Unit) {
        try {
            val activity = AppContext.currentActivity
            if (activity == null) {
                callback(null)
                return
            }

            val result = suspendCancellableCoroutine { continuation ->
                try {
                    val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
                        // 限制视频时长（60秒）
                        putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60)
                        // 设置视频质量（0=低，1=高）
                        putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
                    }

                    val requestCode = ActivityResultBridge.register { resultCode, data ->
                        if (continuation.isActive) {
                            if (resultCode == Activity.RESULT_OK) {
                                val uri = data?.data
                                if (uri != null) {
                                    val media = resolveVideoMedia(uri)
                                    continuation.resume(media)
                                } else {
                                    continuation.resume(null)
                                }
                            } else {
                                continuation.resume(null)
                            }
                        }
                    }

                    activity.startActivityForResult(intent, requestCode)
                } catch (e: Exception) {
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }
            }
            callback(result)
        } catch (e: Exception) {
            callback(null)
        }
    }

    /**
     * 创建临时图片文件
     */
    private fun createTempImageFile(): File {
        val context = AppContext.application
        val dir = File(context.cacheDir, "camera").apply { mkdirs() }
        return File(dir, "photo_${System.currentTimeMillis()}.jpg")
    }

    /**
     * 获取文件的 content:// URI
     */
    private fun getFileUri(file: File): Uri {
        return try {
            val context = AppContext.application
            val authority = "${context.packageName}.fileprovider"
            FileProvider.getUriForFile(context, authority, file)
        } catch (e: Exception) {
            Uri.fromFile(file)
        }
    }

    /**
     * 解析拍照输出的媒体信息
     */
    private fun resolveCapturedMedia(uri: Uri, file: File): CapturedMedia {
        val context = AppContext.application
        var name = file.name
        var size = if (file.exists()) file.length() else -1L
        var mimeType = "image/jpeg"

        // 尝试从 ContentResolver 获取更准确的信息
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) name = cursor.getString(nameIndex) ?: name

                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) {
                        size = cursor.getLong(sizeIndex)
                    }
                }
            }
            context.contentResolver.getType(uri)?.let { mimeType = it }
        } catch (e: Exception) {
            // 查询失败时使用文件信息
        }

        return CapturedMedia(
            path = uri.toString(),
            name = name,
            size = size,
            mimeType = mimeType
        )
    }

    /**
     * 解析录像输出的媒体信息
     */
    private fun resolveVideoMedia(uri: Uri): CapturedMedia {
        val context = AppContext.application
        var name = "video_${System.currentTimeMillis()}.mp4"
        var size = -1L
        var mimeType = "video/mp4"

        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) name = cursor.getString(nameIndex) ?: name

                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) {
                        size = cursor.getLong(sizeIndex)
                    }
                }
            }
            context.contentResolver.getType(uri)?.let { mimeType = it }
        } catch (e: Exception) {
            // 查询失败时使用默认值
        }

        return CapturedMedia(
            path = uri.toString(),
            name = name,
            size = size,
            mimeType = mimeType
        )
    }
}

actual fun provideCamera(): Camera = Camera()
