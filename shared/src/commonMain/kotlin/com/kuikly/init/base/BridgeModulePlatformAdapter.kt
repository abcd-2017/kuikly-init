package com.kuikly.init.base

import com.kuikly.init.common.base.platform.NetworkType
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject

/**
 * OHOS 平台能力桥接适配器
 *
 * OHOS 平台没有 Kotlin source set，平台能力通过 BridgeModule 从 ETS 侧获取。
 * 此类提供同步接口，供 Koin 绑定使用。
 *
 * 注意：此适配器依赖 Native 侧已初始化完成，应在 initKuikly() 之后使用。
 */
class BridgeModulePlatformAdapter {

    private fun currentPagerId(): String {
        return com.tencent.kuikly.core.manager.BridgeManager.currentPageId
    }

    private fun getBridgeModule(): BridgeModule {
        return Utils.bridgeModule(currentPagerId())
    }

    // ---- ContextProvider ----

    fun getCacheDirPath(): String {
        val result = syncCall("getCacheDir", null)
        return parseStringValue(result, "path", "/data/storage/el2/base/haps/entry/cache")
    }

    fun getFilesDirPath(): String {
        val result = syncCall("getFilesDir", null)
        return parseStringValue(result, "path", "/data/storage/el2/base/haps/entry/files")
    }

    fun readAsset(path: String): ByteArray {
        // OHOS 通过 BridgeModule 的 readAssetFile 方法读取
        // 简化实现，返回空
        return ByteArray(0)
    }

    // ---- DeviceInfo ----

    fun getDeviceId(): String {
        val result = syncCall("getDeviceId", null)
        return parseStringValue(result, "deviceId", "ohos-device-id")
    }

    fun getOSVersion(): String {
        val result = syncCall("getOSVersion", null)
        return parseStringValue(result, "version", "HarmonyOS")
    }

    fun getDeviceModel(): String {
        val result = syncCall("getDeviceModel", null)
        return parseStringValue(result, "model", "OHOS Device")
    }

    // ---- FileSystem ----

    fun fileExists(path: String): Boolean {
        val params = JSONObject().apply { put("path", path) }
        val result = syncCall("fileExists", params)
        return parseBooleanValue(result, "exists", false)
    }

    fun fileDelete(path: String): Boolean {
        val params = JSONObject().apply { put("path", path) }
        val result = syncCall("fileDelete", params)
        return parseBooleanValue(result, "success", false)
    }

    // ---- NetworkMonitor ----

    fun isConnected(): Boolean {
        val result = syncCall("isNetworkConnected", null)
        return parseBooleanValue(result, "connected", false)
    }

    fun getNetworkType(): NetworkType {
        val result = syncCall("getNetworkType", null)
        val type = parseStringValue(result, "type", "none")
        return when (type) {
            "wifi" -> NetworkType.WIFI
            "cellular" -> NetworkType.CELLULAR
            else -> NetworkType.NONE
        }
    }

    // ---- 辅助方法 ----

    /**
     * 同步调用 Native 方法
     *
     * 通过 BridgeModule 的 internal 方法实现同步调用。
     */
    private fun syncCall(method: String, params: JSONObject?): String {
        return try {
            val bridgeModule = getBridgeModule()
            bridgeModule.syncCallNativeMethodInternal(method, params)
        } catch (e: Exception) {
            ""
        }
    }

    private fun parseStringValue(result: String, key: String, default: String): String {
        return try {
            if (result.isEmpty()) return default
            val obj = JSONObject(result)
            obj.optString(key, default)
        } catch (e: Exception) {
            default
        }
    }

    private fun parseBooleanValue(result: String, key: String, default: Boolean): Boolean {
        return try {
            if (result.isEmpty()) return default
            val obj = JSONObject(result)
            obj.optBoolean(key, default)
        } catch (e: Exception) {
            default
        }
    }
}
