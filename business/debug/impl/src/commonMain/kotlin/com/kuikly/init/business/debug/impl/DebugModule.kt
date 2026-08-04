package com.kuikly.init.business.debug.impl

import com.kuikly.init.business.debug.api.IDebugService
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * debug 模块 Koin 依赖注入配置
 *
 * 使用方式：
 *   startKoin {
 *       modules(DebugModule)
 *   }
 *
 * 获取实例：
 *   val service: IDebugService = koin.get()
 */
val DebugModule: Module = module {
    single<IDebugService> { DebugServiceImpl() }
}
