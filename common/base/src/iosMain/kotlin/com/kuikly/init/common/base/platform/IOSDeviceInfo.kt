package com.kuikly.init.common.base.platform

import platform.UIKit.UIDevice

class IOSDeviceInfo : DeviceInfo {
    override fun getDeviceId(): String =
        UIDevice.currentDevice.identifierForVendor?.UUIDString ?: "unknown"

    override fun getOSVersion(): String = "iOS ${UIDevice.currentDevice.systemVersion}"

    override fun getDeviceModel(): String = UIDevice.currentDevice.model
}
