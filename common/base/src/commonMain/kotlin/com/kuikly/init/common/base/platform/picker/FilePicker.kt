package com.kuikly.init.common.base.platform.picker

/**
 * 已选择的文件信息
 *
 * @param path 文件路径（平台相关，可能是 URI、绝对路径或沙箱路径）
 * @param name 文件名（含扩展名）
 * @param size 文件大小（字节），未知时为 -1
 * @param mimeType MIME 类型，未知时为 null
 */
data class PickedFile(
    val path: String,
    val name: String,
    val size: Long,
    val mimeType: String?
)

/**
 * 文件选择器能力抽象
 *
 * 提供单选/多选文件、图片、文档的能力。
 * 所有方法均为 suspend 函数，返回空列表表示用户取消选择。
 */
expect class FilePicker {
    /**
     * 选择文件
     *
     * @param mimeType 过滤的 MIME 类型，如 "image/*"、"application/pdf"，默认 "*/*"
     * @param allowMultiple 是否允许多选
     * @return 已选择的文件列表，取消返回空列表
     */
    suspend fun pickFile(mimeType: String = "*/*", allowMultiple: Boolean = false): List<PickedFile>

    /**
     * 选择图片
     *
     * @param allowMultiple 是否允许多选
     * @return 已选择的文件列表，取消返回空列表
     */
    suspend fun pickImage(allowMultiple: Boolean = false): List<PickedFile>

    /**
     * 选择文档（PDF、Word 等）
     *
     * @param allowMultiple 是否允许多选
     * @return 已选择的文件列表，取消返回空列表
     */
    suspend fun pickDocument(allowMultiple: Boolean = false): List<PickedFile>
}

/** 全局访问入口 */
expect fun provideFilePicker(): FilePicker
