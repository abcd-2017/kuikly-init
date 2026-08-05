package com.kuikly.init.common.base.platform

/**
 * OHOS 全局 Context
 *
 * OHOS 平台无 Application 概念，androidContext 保持 null。
 * 平台能力通过 KNOI 注入的 PlatformService 提供。
 */
actual object AppContext {
    actual var androidContext: Any? = null
}
