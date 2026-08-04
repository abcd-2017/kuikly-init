package com.kuikly.init

import android.app.Application
import com.kuikly.init.common.base.platform.ContextProvider
import com.kuikly.init.common.base.platform.DeviceInfo
import com.kuikly.init.common.base.platform.FileSystem
import com.kuikly.init.common.base.platform.NetworkMonitor
import com.kuikly.init.common.base.platform.AndroidContextProvider
import com.kuikly.init.common.base.platform.AndroidDeviceInfo
import com.kuikly.init.common.base.platform.AndroidFileSystem
import com.kuikly.init.common.base.platform.AndroidNetworkMonitor
import com.kuikly.init.business.initTask.InitTaskRunner
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

        // 初始化 Koin
        startKoin {
            androidContext(this@KRApplication)
            modules(
                module { single<ContextProvider> { AndroidContextProvider(this@KRApplication) } }
                + module { single<DeviceInfo> { AndroidDeviceInfo() } }
                + module { single<FileSystem> { AndroidFileSystem() } }
                + module { single<NetworkMonitor> { AndroidNetworkMonitor(this@KRApplication) } }
            )
        }

        // 执行初始化任务
        InitTaskRunner.runAll(
            platformTasks = listOf(
                // AndroidAccountInitTask()  // 后续业务接入时添加
            )
        )
    }

    companion object {
        lateinit var application: Application
    }
}
