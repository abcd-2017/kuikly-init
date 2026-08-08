package com.kuikly.init.common.base.platform

actual class NetworkMonitor {
    actual fun isConnected(): Boolean = true // 桩实现
    actual fun getNetworkType(): NetworkType = NetworkType.WIFI // 桩实现
}

actual fun provideNetworkMonitor(): NetworkMonitor = NetworkMonitor()
