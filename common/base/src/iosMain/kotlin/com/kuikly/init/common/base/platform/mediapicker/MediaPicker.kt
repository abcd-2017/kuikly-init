package com.kuikly.init.common.base.platform.mediapicker

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSFileManager
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.UIKit.UIApplication
import platform.UIKit.UTType
import platform.UIKit.UTTypeImage
import platform.UIKit.UTTypeMovie
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIActivityViewControllerDelegateProtocol
import platform.UIKit.UIActivityViewController
import platform.UniformTypeIdentifiers.UTTypeContent
import platform.darwin.NSObject
import kotlin.coroutines.resume

/**
 * iOS 相册选择器实现
 *
 * 基于 UIDocumentPickerViewController，通过 suspendCancellableCoroutine 包装 delegate 回调。
 * 支持单选/多选图片和视频。
 *
 * 注意：
 * - 需要在 Info.plist 中配置 NSPhotoLibraryUsageDescription。
 * - 选择器以副本模式（asCopy=true）返回文件，原始文件不受影响。
 */
actual class MediaPicker {

    actual suspend fun pickMedia(
        mediaType: MediaMediaType,
        allowMultiple: Boolean,
        callback: (List<PickedMedia>) -> Unit
    ) {
        val utTypes = when (mediaType) {
            MediaMediaType.IMAGE -> listOf(UTTypeImage)
            MediaMediaType.VIDEO -> listOf(UTTypeMovie)
            MediaMediaType.ALL -> listOf(UTTypeImage, UTTypeMovie)
        }

        val result = suspendCancellableCoroutine { continuation ->
            NSOperationQueue.mainQueue.addOperationWithBlock {
                try {
                    val picker = platform.UIKit.UIDocumentPickerViewController(
                        forOpeningContentTypes = utTypes,
                        asCopy = true
                    )
                    picker.allowsMultipleSelection = allowMultiple

                    val delegate = object : NSObject(), platform.UIKit.UIDocumentPickerDelegateProtocol {
                        override fun documentPicker(
                            controller: platform.UIKit.UIDocumentPickerViewController,
                            didPickDocumentsAtUrls: List<Any>
                        ) {
                            val files = didPickDocumentsAtUrls.mapNotNull { url ->
                                (url as? NSURL)?.let { resolveMediaInfo(it) }
                            }
                            continuation.resume(files)
                        }

                        override fun documentPickerWasCancelled(
                            controller: platform.UIKit.UIDocumentPickerViewController
                        ) {
                            continuation.resume(emptyList())
                        }
                    }

                    picker.delegate = delegate

                    getRootViewController()?.presentViewController(
                        picker, animated = true, completion = null
                    ) ?: continuation.resume(emptyList())
                } catch (e: Exception) {
                    continuation.resume(emptyList())
                }
            }
        }
        callback(result)
    }

    /**
     * 从 NSURL 解析媒体信息
     */
    private fun resolveMediaInfo(url: NSURL): PickedMedia {
        val name = url.lastPathComponent ?: "unknown"
        val ext = url.pathExtension

        // 通过文件扩展名推断 MIME 类型
        val mimeType = if (ext != null && ext.isNotEmpty()) {
            UTType.typeWithFilenameExtension(ext)?.identifier ?: "application/octet-stream"
        } else {
            "application/octet-stream"
        }

        return PickedMedia(
            path = url.absoluteString ?: url.path ?: "",
            name = name,
            size = getFileSize(url),
            mimeType = mimeType,
            duration = null
        )
    }

    /**
     * 通过 NSFileManager 获取文件大小
     */
    private fun getFileSize(url: NSURL): Long {
        return try {
            val filePath = url.path ?: return -1L
            val attributes = NSFileManager.defaultManager.attributesOfItemAtPath(filePath, null)
            if (attributes != null) {
                val size = attributes[platform.Foundation.NSFileSize] as? platform.Foundation.NSNumber
                size?.longValue ?: -1L
            } else {
                -1L
            }
        } catch (e: Exception) {
            -1L
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

actual fun provideMediaPicker(): MediaPicker = MediaPicker()
