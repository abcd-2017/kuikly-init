package com.kuikly.init.common.base.platform

import android.os.Build
import java.util.UUID

actual class DeviceInfo {
    actual fun getDeviceId(): String = UUID.randomUUID().toString()
    actual fun getOSVersion(): String = "Android ${Build.VERSION.RELEASE}"
    actual fun getDeviceModel(): String = "${Build.MANUFACTURER} ${Build.MODEL}"
}
