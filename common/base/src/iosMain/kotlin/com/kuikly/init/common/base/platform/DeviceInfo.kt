package com.kuikly.init.common.base.platform

import platform.UIKit.UIDevice

actual class DeviceInfo {
    actual fun getDeviceId(): String = UIDevice.currentDevice.identifierForVendor?.UUIDString ?: "unknown"
    actual fun getOSVersion(): String = "iOS ${UIDevice.currentDevice.systemVersion}"
    actual fun getDeviceModel(): String = UIDevice.currentDevice.model
}
