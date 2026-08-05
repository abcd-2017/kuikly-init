package com.kuikly.init.common.base.platform

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

actual class DeviceInfo {
    actual fun getDeviceId(): String =
        getIOHOSPlatformServiceApi()?.getDeviceId() ?: "ohos-device-id"

    actual fun getOSVersion(): String =
        getIOHOSPlatformServiceApi()?.getOSVersion() ?: "HarmonyOS"

    actual fun getDeviceModel(): String =
        getIOHOSPlatformServiceApi()?.getDeviceModel() ?: "OHOS Device"
}
