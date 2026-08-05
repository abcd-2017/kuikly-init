package com.kuikly.init.common.base.platform

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

actual class NetworkMonitor {
    actual fun isConnected(): Boolean =
        getIOHOSPlatformServiceApi()?.isNetworkConnected() ?: false

    actual fun getNetworkType(): NetworkType {
        val type = getIOHOSPlatformServiceApi()?.getNetworkType() ?: "none"
        return when (type) {
            "wifi" -> NetworkType.WIFI
            "cellular" -> NetworkType.CELLULAR
            else -> NetworkType.NONE
        }
    }
}
