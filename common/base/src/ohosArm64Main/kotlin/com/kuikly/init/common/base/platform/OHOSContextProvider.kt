package com.kuikly.init.common.base.platform

/**
 * OHOS 平台 ContextProvider 桩实现
 *
 * OHOS 不使用 Koin DI，平台能力通过 BridgeModule 从 ETS 侧获取。
 * 此实现仅用于编译通过，实际能力由 BridgeModulePlatformAdapter 提供。
 */
class OHOSContextProvider : ContextProvider {
    override fun getCacheDirPath(): String {
        throw UnsupportedOperationException("ContextProvider is not supported on OHOS platform, use BridgeModulePlatformAdapter instead")
    }

    override fun getFilesDirPath(): String {
        throw UnsupportedOperationException("ContextProvider is not supported on OHOS platform, use BridgeModulePlatformAdapter instead")
    }

    override fun readAsset(path: String): ByteArray {
        throw UnsupportedOperationException("ContextProvider is not supported on OHOS platform, use BridgeModulePlatformAdapter instead")
    }
}
