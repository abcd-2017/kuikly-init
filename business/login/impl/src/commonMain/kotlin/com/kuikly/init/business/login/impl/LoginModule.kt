package com.kuikly.init.business.login.impl

import com.kuikly.init.business.login.api.ILoginService
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * 登录模块 Koin 依赖注入配置
 *
 * 使用方式：
 *   startKoin {
 *       modules(LoginModule)
 *   }
 *
 * 获取实例：
 *   val loginService: ILoginService = koin.get()
 */
val LoginModule: Module = module {
    single<ILoginService> { LoginServiceImpl() }
}
