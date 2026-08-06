package com.kuikly.init.common.base.platform.camera

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSFileManager
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerMediaType
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerMediaURL
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.delegate
import platform.darwin.NSObject
import kotlin.coroutines.resume

/**
 * iOS 相机实现
 *
 * 基于 UIImagePickerController，通过 suspendCancellableCoroutine 包装 delegate 回调。
 * 支持拍照和录制视频。
 *
 * 注意：
 * - 需要在 Info.plist 中配置 NSCameraUsageDescription 和 NSMicrophoneUsageDescription。
 * - 拍照返回 JPEG 格式图片，视频返回 MP4 格式文件。
 * - 所有 UI 操作通过 NSOperationQueue.mainQueue 调度到主线程执行。
 */
actual class Camera {

    actual suspend fun capturePhoto(callback: (CapturedMedia?) -> Unit) {
        pickWithSource(
            sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera,
            mediaType = "public.image",
            callback = callback
        )
    }

    actual suspend fun recordVideo(callback: (CapturedMedia?) -> Unit) {
        pickWithSource(
            sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera,
            mediaType = "public.movie",
            callback = callback
        )
    }

    /**
     * 使用指定的源类型和媒体类型打开选择器
     */
    private suspend fun pickWithSource(
        sourceType: UIImagePickerControllerSourceType,
        mediaType: String,
        callback: (CapturedMedia?) -> Unit
    ) {
        val result = suspendCancellableCoroutine { continuation ->
            NSOperationQueue.mainQueue.addOperationWithBlock {
                try {
                    val picker = UIImagePickerController()
                    picker.sourceType = sourceType
                    picker.mediaTypes = listOf(mediaType)
                    // 设置视频质量
                    if (mediaType == "public.movie") {
                        picker.videoQuality = platform.UIKit.UIImagePickerControllerQualityType.UIImagePickerControllerQualityTypeMedium
                    }

                    val delegate = object : NSObject(), UIImagePickerControllerDelegateProtocol {
                        override fun imagePickerController(
                            picker: UIImagePickerController,
                            didFinishPickingMediaWithInfo: Map<Any?, *>
                        ) {
                            try {
                                val media = if (mediaType == "public.image") {
                                    // 处理图片
                                    val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
                                    if (image != null) {
                                        saveImageToTempFile(image)
                                    } else {
                                        null
                                    }
                                } else {
                                    // 处理视频
                                    val url = didFinishPickingMediaWithInfo[UIImagePickerControllerMediaURL] as? NSURL
                                    if (url != null) {
                                        resolveVideoInfo(url)
                                    } else {
                                        null
                                    }
                                }
                                picker.dismissViewControllerAnimated(true, completion = null)
                                continuation.resume(media)
                            } catch (e: Exception) {
                                picker.dismissViewControllerAnimated(true, completion = null)
                                continuation.resume(null)
                            }
                        }

                        override fun imagePickerControllerDidCancel(
                            picker: UIImagePickerController
                        ) {
                            picker.dismissViewControllerAnimated(true, completion = null)
                            continuation.resume(null)
                        }
                    }

                    picker.delegate = delegate

                    getRootViewController()?.presentViewController(
                        picker, animated = true, completion = null
                    ) ?: continuation.resume(null)
                } catch (e: Exception) {
                    continuation.resume(null)
                }
            }
        }
        callback(result)
    }

    /**
     * 将 UIImage 保存为临时 JPEG 文件
     */
    private fun saveImageToTempFile(image: UIImage): CapturedMedia? {
        return try {
            val jpegData = UIImageJPEGRepresentation(image, 0.9) ?: return null
            val dir = NSTemporaryDirectory()
            val name = "photo_${NSUUID().UUIDString}.jpg"
            val filePath = dir + name

            // 将 NSData 写入文件
            val nsData = jpegData as platform.Foundation.NSData
            val success = nsData.writeToFile(filePath, atomically = true)
            if (!success) return null

            val fileSize = getFileSize(filePath)

            CapturedMedia(
                path = "file://$filePath",
                name = name,
                size = fileSize,
                mimeType = "image/jpeg"
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 解析视频文件信息
     */
    private fun resolveVideoInfo(url: NSURL): CapturedMedia? {
        return try {
            val path = url.path ?: return null
            val name = url.lastPathComponent ?: "video_${NSUUID().UUIDString}.mp4"
            val fileSize = getFileSize(path)

            CapturedMedia(
                path = url.absoluteString ?: "file://$path",
                name = name,
                size = fileSize,
                mimeType = "video/quicktime"
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 获取文件大小
     */
    private fun getFileSize(path: String): Long {
        return try {
            val attributes = NSFileManager.defaultManager.attributesOfItemAtPath(path, null)
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

actual fun provideCamera(): Camera = Camera()
