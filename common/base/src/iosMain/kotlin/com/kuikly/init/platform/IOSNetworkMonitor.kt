package com.kuikly.init.platform

import com.kuikly.init.common.base.platform.NetworkMonitor
import com.kuikly.init.common.base.platform.NetworkType

/**
 * iOS 网络状态监控（桩实现）
 *
 * 完整实现需要 SCNetworkReachability 或 NWPathMonitor，
 * 后续按需补充。
 */
class IOSNetworkMonitor : NetworkMonitor {
    override fun isConnected(): Boolean = true // 桩实现

    override fun getNetworkType(): NetworkType = NetworkType.WIFI // 桩实现
}
