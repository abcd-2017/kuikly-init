package com.kuikly.init.common.base.platform.camera

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

/**
 * OHOS 相机实现（通过 KNOI 调用 ETS 侧）
 *
 * 基于 @ohos.multimedia.cameraPicker，由 ArkTS 侧实现具体逻辑。
 * 返回 JSON 字符串，Kotlin 侧手动解析为 CapturedMedia。
 */
actual class Camera {

    private val service get() = getIOHOSPlatformServiceApi()

    actual suspend fun capturePhoto(callback: (CapturedMedia?) -> Unit) {
        try {
            service?.capturePhoto { json ->
                callback(parseCapturedMedia(json))
            }
        } catch (e: Exception) {
            callback(null)
        }
    }

    actual suspend fun recordVideo(callback: (CapturedMedia?) -> Unit) {
        try {
            service?.recordVideo { json ->
                callback(parseCapturedMedia(json))
            }
        } catch (e: Exception) {
            callback(null)
        }
    }

    /**
     * 解析 ETS 侧返回的 JSON 字符串
     *
     * 格式：{"path":"...","name":"...","size":123,"mimeType":"image/jpeg"}
     * 使用简单字符串解析，避免引入 kotlinx.serialization 依赖。
     */
    private fun parseCapturedMedia(json: String): CapturedMedia? {
        return try {
            val cleaned = json.removePrefix("{").removeSuffix("}")
            if (cleaned.isBlank() || cleaned == "{}") return null

            val map = mutableMapOf<String, String>()
            val pairs = cleaned.split(",")
            for (pair in pairs) {
                val keyValue = pair.split(":")
                if (keyValue.size >= 2) {
                    val key = keyValue[0].trim().removeSurrounding("\"")
                    val value = keyValue.drop(1).joinToString(":").trim().removeSurrounding("\"")
                    map[key] = value
                }
            }

            val path = map["path"] ?: return null
            val name = map["name"] ?: "unknown"
            val size = map["size"]?.toLongOrNull() ?: -1L
            val mimeType = map["mimeType"] ?: "application/octet-stream"

            CapturedMedia(
                path = path,
                name = name,
                size = size,
                mimeType = mimeType
            )
        } catch (e: Exception) {
            null
        }
    }
}

actual fun provideCamera(): Camera = Camera()
