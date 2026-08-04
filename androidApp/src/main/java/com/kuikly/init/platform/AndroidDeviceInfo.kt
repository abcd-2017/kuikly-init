package com.kuikly.init.platform

import android.os.Build
import com.kuikly.init.common.base.platform.DeviceInfo
import java.util.UUID

class AndroidDeviceInfo : DeviceInfo {
    override fun getDeviceId(): String = UUID.randomUUID().toString() // 简化实现
    override fun getOSVersion(): String = "Android ${Build.VERSION.RELEASE}"
    override fun getDeviceModel(): String = "${Build.MANUFACTURER} ${Build.MODEL}"
}
