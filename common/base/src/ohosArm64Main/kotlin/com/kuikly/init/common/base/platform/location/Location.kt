package com.kuikly.init.common.base.platform.location

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

/**
 * OHOS 地理位置实现（通过 KNOI 调用 ETS 侧）
 *
 * 基于 @ohos.geoLocationManager，由 ArkTS 侧实现具体逻辑。
 */
actual class LocationProvider {

    private val service get() = getIOHOSPlatformServiceApi()

    actual fun requestPermission(callback: (Boolean) -> Unit) {
        try {
            val result = service?.requestLocationPermission()
            callback(result == true)
        } catch (e: Exception) {
            callback(false)
        }
    }

    actual fun getCurrentLocation(accuracy: LocationAccuracy, callback: (Location?) -> Unit) {
        try {
            val accuracyValue = when (accuracy) {
                LocationAccuracy.COARSE -> 0
                LocationAccuracy.BALANCED -> 1
                LocationAccuracy.PRECISE -> 2
            }
            service?.getCurrentLocation(accuracyValue) { json ->
                callback(parseLocationJson(json))
            }
        } catch (e: Exception) {
            callback(null)
        }
    }

    /**
     * 解析 OHOS 返回的 JSON 位置信息
     *
     * 格式：{"latitude":31.23,"longitude":121.47,"altitude":10.0,"accuracy":5.0,"speed":0.0,"timestamp":1234567890}
     */
    private fun parseLocationJson(json: String): Location? {
        return try {
            val map = mutableMapOf<String, String>()
            val cleaned = json.removePrefix("{").removeSuffix("}")
            if (cleaned.isBlank() == false) {
                val pairs = cleaned.split(",")
                for (pair in pairs) {
                    val keyValue = pair.split(":")
                    if (keyValue.size == 2) {
                        val key = keyValue[0].trim().removeSurrounding("\"")
                        val value = keyValue[1].trim().removeSurrounding("\"")
                        map[key] = value
                    }
                }
            }
            if (map.isEmpty()) return null
            Location(
                latitude = map["latitude"]?.toDoubleOrNull() ?: return null,
                longitude = map["longitude"]?.toDoubleOrNull() ?: return null,
                altitude = map["altitude"]?.toDoubleOrNull(),
                accuracy = map["accuracy"]?.toFloatOrNull(),
                speed = map["speed"]?.toFloatOrNull(),
                timestamp = map["timestamp"]?.toLongOrNull()
            )
        } catch (e: Exception) {
            null
        }
    }
}

actual fun provideLocationProvider(): LocationProvider = LocationProvider()
