package com.kuikly.init

import android.app.Application
import com.kuikly.init.business.debug.impl.DebugModule
import com.kuikly.init.business.initTask.InitTaskRunner
import com.kuikly.init.common.base.platform.AppContext
import com.kuikly.init.common.base.platform.ContextProvider
import com.kuikly.init.common.base.platform.DeviceInfo
import com.kuikly.init.common.base.platform.FileSystem
import com.kuikly.init.common.base.platform.NetworkMonitor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class KRApplication : Application() {

    init {
        application = this
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        AppContext.init(this)

        startKoin {
            androidContext(this@KRApplication)
            modules(
                module { single<ContextProvider> { ContextProvider(this@KRApplication) } }
                + module { single<DeviceInfo> { DeviceInfo() } }
                + module { single<FileSystem> { FileSystem() } }
                + module { single<NetworkMonitor> { NetworkMonitor(this@KRApplication) } }
                + DebugModule
            )
        }

        InitTaskRunner.runAll(platformTasks = listOf())
    }

    companion object {
        lateinit var application: Application
    }
}
