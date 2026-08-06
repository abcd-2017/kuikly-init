package com.kuikly.init.common.base.platform.mediapicker

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

/**
 * OHOS 相册选择器实现（通过 KNOI 调用 ETS 侧）
 *
 * 基于 @ohos.file.picker，由 ArkTS 侧实现具体逻辑。
 * 返回 JSON 字符串，Kotlin 侧手动解析为 PickedMedia 列表。
 */
actual class MediaPicker {

    private val service get() = getIOHOSPlatformServiceApi()

    actual suspend fun pickMedia(
        mediaType: MediaMediaType,
        allowMultiple: Boolean,
        callback: (List<PickedMedia>) -> Unit
    ) {
        try {
            val mediaTypeValue = when (mediaType) {
                MediaMediaType.IMAGE -> 0
                MediaMediaType.VIDEO -> 1
                MediaMediaType.ALL -> 2
            }
            service?.pickMedia(mediaTypeValue, allowMultiple) { json ->
                callback(parseResult(json))
            }
        } catch (e: Exception) {
            callback(emptyList())
        }
    }

    /**
     * 解析 ETS 侧返回的 JSON 字符串
     *
     * 格式：{"files":[{"path":"...","name":"...","size":123,"mimeType":"..."}]}
     * 使用简单字符串解析，避免引入 kotlinx.serialization 依赖。
     */
    private fun parseResult(jsonStr: String): List<PickedMedia> {
        try {
            val files = mutableListOf<PickedMedia>()
            // 提取 "files" 数组内容
            val filesArrayStart = jsonStr.indexOf("[")
            val filesArrayEnd = jsonStr.lastIndexOf("]")
            if (filesArrayStart < 0 || filesArrayEnd <= filesArrayStart) {
                return emptyList()
            }
            val arrayContent = jsonStr.substring(filesArrayStart + 1, filesArrayEnd)
            if (arrayContent.isBlank()) return emptyList()

            // 按 "},{" 分割各个文件对象
            val objects = arrayContent.split("},{")
            for (obj in objects) {
                val cleanObj = obj.removePrefix("{").removeSuffix("}")
                val path = extractStringValue(cleanObj, "path") ?: continue
                val name = extractStringValue(cleanObj, "name") ?: "unknown"
                val size = extractLongValue(cleanObj, "size")
                val mimeType = extractStringValue(cleanObj, "mimeType") ?: "application/octet-stream"
                files.add(PickedMedia(path, name, size, mimeType, null))
            }
            return files
        } catch (e: Exception) {
            return emptyList()
        }
    }

    /**
     * 从 JSON 对象字符串中提取字符串值
     */
    private fun extractStringValue(json: String, key: String): String? {
        val keyPattern = "\"$key\""
        val keyIndex = json.indexOf(keyPattern)
        if (keyIndex < 0) return null
        val colonIndex = json.indexOf(":", keyIndex + keyPattern.length)
        if (colonIndex < 0) return null
        val afterColon = json.substring(colonIndex + 1).trimStart()
        if (afterColon.startsWith("null")) return null
        if (!afterColon.startsWith("\"")) return null
        val valueStart = 1
        val valueEnd = afterColon.indexOf("\"", valueStart)
        if (valueEnd < 0) return null
        return afterColon.substring(valueStart, valueEnd)
    }

    /**
     * 从 JSON 对象字符串中提取长整数值
     */
    private fun extractLongValue(json: String, key: String): Long {
        val keyPattern = "\"$key\""
        val keyIndex = json.indexOf(keyPattern)
        if (keyIndex < 0) return -1L
        val colonIndex = json.indexOf(":", keyIndex + keyPattern.length)
        if (colonIndex < 0) return -1L
        val afterColon = json.substring(colonIndex + 1).trimStart()
        val valueEnd = afterColon.indexOfFirst { it == ',' || it == '}' }
        if (valueEnd < 0) return -1L
        return afterColon.substring(0, valueEnd).trim().toLongOrNull() ?: -1L
    }
}

actual fun provideMediaPicker(): MediaPicker = MediaPicker()
