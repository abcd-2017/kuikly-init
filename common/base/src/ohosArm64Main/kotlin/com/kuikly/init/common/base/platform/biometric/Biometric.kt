package com.kuikly.init.common.base.platform.biometric

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

/**
 * OHOS 生物识别实现（通过 KNOI 调用 ETS 侧）
 *
 * 基于 @ohos.userAuth，由 ArkTS 侧实现具体逻辑。
 */
actual class Biometric {

    private val service get() = getIOHOSPlatformServiceApi()

    actual fun isSupported(): Boolean {
        return try {
            service?.isBiometricSupported() ?: false
        } catch (e: Exception) {
            false
        }
    }

    actual fun getSupportedTypes(): Set<BiometricType> {
        return try {
            val json = service?.getSupportedBiometricTypes() ?: "{}"
            parseBiometricTypes(json)
        } catch (e: Exception) {
            emptySet()
        }
    }

    actual fun authenticate(title: String, cancelText: String, callback: (String) -> Unit) {
        try {
            service?.authenticate(title, cancelText) { resultJson ->
                callback(resultJson)
            }
        } catch (e: Exception) {
            callback("{\"result\":\"FAILED\"}")
        }
    }

    /**
     * 解析 JSON 为支持的生物识别类型集合
     *
     * JSON 格式：{"types":["FACE","FINGERPRINT","PIN"]}
     */
    private fun parseBiometricTypes(json: String): Set<BiometricType> {
        return try {
            val types = mutableSetOf<BiometricType>()
            val typesStart = json.indexOf("\"types\"")
            if (typesStart < 0) return types
            val arrayStart = json.indexOf("[", typesStart)
            val arrayEnd = json.indexOf("]", arrayStart)
            if (arrayStart < 0 || arrayEnd < 0) return types
            val arrayContent = json.substring(arrayStart + 1, arrayEnd)
            val items = arrayContent.split(",")
            for (item in items) {
                val trimmed = item.trim().trim('"').uppercase()
                when (trimmed) {
                    "FACE" -> types.add(BiometricType.FACE)
                    "FINGERPRINT" -> types.add(BiometricType.FINGERPRINT)
                    "PIN" -> types.add(BiometricType.PIN)
                }
            }
            types
        } catch (e: Exception) {
            emptySet()
        }
    }
}

actual fun provideBiometric(): Biometric = Biometric()
