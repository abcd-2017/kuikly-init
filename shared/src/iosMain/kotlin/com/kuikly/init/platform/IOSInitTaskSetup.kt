package com.kuikly.init.platform

import com.kuikly.init.business.initTask.InitTaskRunner
import com.kuikly.init.common.base.platform.ContextProvider
import com.kuikly.init.common.base.platform.DeviceInfo
import com.kuikly.init.common.base.platform.FileSystem
import com.kuikly.init.common.base.platform.NetworkMonitor
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun setupIOSKoin() {
    startKoin {
        modules(
            module { single<ContextProvider> { ContextProvider() } }
            + module { single<DeviceInfo> { DeviceInfo() } }
            + module { single<FileSystem> { FileSystem() } }
            + module { single<NetworkMonitor> { NetworkMonitor() } }
        )
    }

    InitTaskRunner.runAll(emptyList())
}
