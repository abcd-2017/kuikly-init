package com.kuikly.init.common.base.platform.scan

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

/**
 * OHOS 扫码实现（通过 KNOI 调用 ETS 侧）
 *
 * 基于 @kit.ScanKit，由 ArkTS 侧实现具体逻辑。
 */
actual class Scanner {

    private val service get() = getIOHOSPlatformServiceApi()

    actual fun startScan(callback: (ScanResult?) -> Unit) {
        try {
            service?.startScan { json ->
                callback(parseScanResultJson(json))
            }
        } catch (e: Exception) {
            callback(null)
        }
    }

    /**
     * 解析 OHOS 返回的 JSON 扫码结果
     *
     * 格式：{"content":"https://example.com","format":"QR_CODE"}
     */
    private fun parseScanResultJson(json: String): ScanResult? {
        return try {
            val map = mutableMapOf<String, String>()
            val cleaned = json.removePrefix("{").removeSuffix("}")
            if (cleaned.isBlank()) return null
            val pairs = cleaned.split(",")
            for (pair in pairs) {
                val keyValue = pair.split(":")
                if (keyValue.size == 2) {
                    val key = keyValue[0].trim().removeSurrounding("\"")
                    val value = keyValue[1].trim().removeSurrounding("\"")
                    map[key] = value
                }
            }
            val content = map["content"] ?: return null
            ScanResult(
                content = content,
                format = map["format"] ?: "UNKNOWN"
            )
        } catch (e: Exception) {
            null
        }
    }
}

actual fun provideScanner(): Scanner = Scanner()
