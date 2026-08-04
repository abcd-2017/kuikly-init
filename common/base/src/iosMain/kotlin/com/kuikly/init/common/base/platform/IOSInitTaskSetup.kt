package com.kuikly.init.common.base.platform

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
            module { single<ContextProvider> { IOSContextProvider() } }
            + module { single<DeviceInfo> { IOSDeviceInfo() } }
            + module { single<FileSystem> { IOSFileSystem() } }
            + module { single<NetworkMonitor> { IOSNetworkMonitor() } }
        )
    }
    InitTaskRunner.runAll(emptyList())
}
