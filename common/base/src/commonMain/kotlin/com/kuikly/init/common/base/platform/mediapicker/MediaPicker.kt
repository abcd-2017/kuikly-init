package com.kuikly.init.common.base.platform.mediapicker

/**
 * 已选择的媒体信息
 *
 * @param path 文件路径（平台相关，可能是 URI、绝对路径或沙箱路径）
 * @param name 文件名（含扩展名）
 * @param size 文件大小（字节），未知时为 -1
 * @param mimeType MIME 类型，如 "image/jpeg"、"video/mp4"
 * @param duration 视频时长（毫秒），图片时为 null
 */
data class PickedMedia(
    val path: String,
    val name: String,
    val size: Long,
    val mimeType: String,
    val duration: Long? = null
)

/** 媒体选择类型 */
enum class MediaMediaType { IMAGE, VIDEO, ALL }

/**
 * 相册选择能力抽象
 *
 * 提供从相册选择图片/视频的能力。
 * 所有方法通过回调返回结果，取消时回调空列表。
 */
expect class MediaPicker {
    /**
     * 选择媒体（图片/视频）
     *
     * @param mediaType 媒体类型过滤（图片/视频/全部）
     * @param allowMultiple 是否允许多选
     * @param callback 回调返回已选择的媒体列表，取消或失败返回空列表
     */
    suspend fun pickMedia(
        mediaType: MediaMediaType = MediaMediaType.IMAGE,
        allowMultiple: Boolean = false,
        callback: (List<PickedMedia>) -> Unit
    )
}

/** 全局访问入口 */
expect fun provideMediaPicker(): MediaPicker
