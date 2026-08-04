package com.kuikly.init.common.base.platform

/**
 * OHOS 平台 FileSystem 桩实现
 *
 * OHOS 不使用 Koin DI，平台能力通过 BridgeModule 从 ETS 侧获取。
 * 此实现仅用于编译通过，实际能力由 BridgeModulePlatformAdapter 提供。
 */
class OHOSFileSystem : FileSystem {
    override fun readFile(path: String): ByteArray {
        throw UnsupportedOperationException("FileSystem is not supported on OHOS platform, use BridgeModulePlatformAdapter instead")
    }

    override fun writeFile(path: String, data: ByteArray) {
        throw UnsupportedOperationException("FileSystem is not supported on OHOS platform, use BridgeModulePlatformAdapter instead")
    }

    override fun exists(path: String): Boolean {
        throw UnsupportedOperationException("FileSystem is not supported on OHOS platform, use BridgeModulePlatformAdapter instead")
    }

    override fun delete(path: String): Boolean {
        throw UnsupportedOperationException("FileSystem is not supported on OHOS platform, use BridgeModulePlatformAdapter instead")
    }
}
