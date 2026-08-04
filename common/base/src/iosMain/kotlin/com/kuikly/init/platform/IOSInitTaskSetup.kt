package com.kuikly.init.platform

import com.kuikly.init.business.initTask.InitTaskRunner
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * iOS 端 Koin + InitTask 初始化入口
 *
 * 在 KuiklyRenderViewPage.makeUIViewController() 中由 Swift 调用。
 */
fun setupIOSKoin() {
    startKoin {
        modules(
            module { single<com.kuikly.init.common.base.platform.ContextProvider> { IOSContextProvider() } }
            + module { single<com.kuikly.init.common.base.platform.DeviceInfo> { IOSDeviceInfo() } }
            + module { single<com.kuikly.init.common.base.platform.FileSystem> { IOSFileSystem() } }
            + module { single<com.kuikly.init.common.base.platform.NetworkMonitor> { IOSNetworkMonitor() } }
        )
    }
    InitTaskRunner.runAll(emptyList())
}
