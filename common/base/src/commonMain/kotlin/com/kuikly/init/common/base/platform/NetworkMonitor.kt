package com.kuikly.init.common.base.platform

/**
 * 网络状态抽象
 */
expect class NetworkMonitor {
    /** 当前是否联网 */
    fun isConnected(): Boolean

    /** 当前网络类型：wifi / cellular / none */
    fun getNetworkType(): NetworkType
}

/** 全局访问入口 */
expect fun provideNetworkMonitor(): NetworkMonitor

enum class NetworkType {
    WIFI,
    CELLULAR,
    NONE
}
