package com.kuikly.init.common.base.platform.camera

/**
 * 已拍摄的媒体信息
 *
 * @param path 文件路径（平台相关，可能是 URI、绝对路径或沙箱路径）
 * @param name 文件名（含扩展名）
 * @param size 文件大小（字节），未知时为 -1
 * @param mimeType MIME 类型，如 "image/jpeg"、"video/mp4"
 */
data class CapturedMedia(
    val path: String,
    val name: String,
    val size: Long,
    val mimeType: String
)

/**
 * 拍照/录像能力抽象
 *
 * 提供调用系统相机拍照和录制视频的能力。
 * 所有方法通过回调返回结果，取消时回调 null。
 */
expect class Camera {
    /**
     * 拍照
     *
     * @param callback 回调返回拍摄的媒体信息，取消或失败返回 null
     */
    suspend fun capturePhoto(callback: (CapturedMedia?) -> Unit)

    /**
     * 录制视频
     *
     * @param callback 回调返回录制的媒体信息，取消或失败返回 null
     */
    suspend fun recordVideo(callback: (CapturedMedia?) -> Unit)
}

/** 全局访问入口 */
expect fun provideCamera(): Camera
