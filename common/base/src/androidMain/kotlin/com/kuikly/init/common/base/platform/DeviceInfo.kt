package com.kuikly.init.common.base.platform

import android.os.Build
import java.util.UUID

/**
 * Android 平台 DeviceInfo 实现
 */
class AndroidDeviceInfo : DeviceInfo {
    override fun getDeviceId(): String = UUID.randomUUID().toString() // 简化实现
    override fun getOSVersion(): String = "Android ${Build.VERSION.RELEASE}"
    override fun getDeviceModel(): String = "${Build.MANUFACTURER} ${Build.MODEL}"
}
