package com.kuikly.init.common.base.platform.picker

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIViewController
import platform.UniformTypeIdentifiers.UTType
import platform.UniformTypeIdentifiers.UTTypeContent
import platform.UniformTypeIdentifiers.UTTypeImage
import platform.UniformTypeIdentifiers.UTTypePDF
import platform.darwin.NSObject
import kotlin.coroutines.resume

/**
 * iOS 文件选择器实现
 *
 * 基于 UIDocumentPickerViewController，通过 suspendCancellableCoroutine 包装 delegate 回调。
 * 支持单选/多选，可按 MIME 类型过滤。
 */
actual class FilePicker {

    actual suspend fun pickFile(mimeType: String, allowMultiple: Boolean): List<PickedFile> {
        val utTypes = mimeTypeToUTTypes(mimeType)
        return pickWithTypes(utTypes, allowMultiple)
    }

    actual suspend fun pickImage(allowMultiple: Boolean): List<PickedFile> {
        return pickWithTypes(listOf(UTTypeImage), allowMultiple)
    }

    actual suspend fun pickDocument(allowMultiple: Boolean): List<PickedFile> {
        // 文档类型：PDF + 通用内容类型
        return pickWithTypes(listOf(UTTypePDF, UTTypeContent), allowMultiple)
    }

    /**
     * 使用指定的 UTType 列表打开文件选择器
     */
    private suspend fun pickWithTypes(
        types: List<UTType>,
        allowMultiple: Boolean
    ): List<PickedFile> = suspendCancellableCoroutine { continuation ->
        NSOperationQueue.mainQueue.addOperationWithBlock {
            try {
                val picker = UIDocumentPickerViewController(
                    forOpeningContentTypes = types,
                    asCopy = true
                )
                picker.allowsMultipleSelection = allowMultiple

                val delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
                    override fun documentPicker(
                        controller: UIDocumentPickerViewController,
                        didPickDocumentsAtUrls: List<Any>
                    ) {
                        val files = didPickDocumentsAtUrls.mapNotNull { url ->
                            (url as? NSURL)?.let { resolveFileInfo(it) }
                        }
                        continuation.resume(files)
                    }

                    override fun documentPickerWasCancelled(
                        controller: UIDocumentPickerViewController
                    ) {
                        continuation.resume(emptyList())
                    }
                }

                picker.delegate = delegate

                getRootViewController()?.presentViewController(
                    picker, animated = true, completion = null
                )
            } catch (e: Exception) {
                continuation.resume(emptyList())
            }
        }
    }

    /**
     * 从 NSURL 解析文件信息
     */
    private fun resolveFileInfo(url: NSURL): PickedFile {
        val name = url.lastPathComponent ?: "unknown"
        val ext = url.pathExtension

        // 通过文件扩展名推断 MIME 类型
        val mimeType = if (ext != null && ext.isNotEmpty()) {
            UTType.typeWithFilenameExtension(ext)?.identifier
        } else {
            null
        }

        return PickedFile(
            path = url.absoluteString ?: url.path ?: "",
            name = name,
            size = -1L, // TODO: 通过 NSFileManager 获取文件大小
            mimeType = mimeType
        )
    }

    /**
     * 将 MIME 类型字符串转换为 UTType 列表
     *
     * 支持常见类型：image/*、application/pdf、text/* 等。
     * 无法识别的类型返回 UTTypeContent 作为兜底。
     */
    private fun mimeTypeToUTTypes(mimeType: String): List<UTType> {
        if (mimeType == "*/*") return listOf(UTTypeContent)

        return when {
            mimeType.startsWith("image/") -> listOf(UTTypeImage)
            mimeType.startsWith("video/") -> listOf(UTTypeContent)
            mimeType.startsWith("audio/") -> listOf(UTTypeContent)
            mimeType == "application/pdf" -> listOf(UTTypePDF)
            mimeType.startsWith("text/") -> listOf(UTTypeContent)
            else -> listOf(UTTypeContent)
        }
    }

    private fun getRootViewController(): UIViewController? {
        val window = UIApplication.sharedApplication.keyWindow
            ?: UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow
        return window?.rootViewController
    }
}

actual fun provideFilePicker(): FilePicker = FilePicker()
