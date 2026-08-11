package com.kuikly.init.common.base.platform

/**
 * iOS 全局 Context
 *
 * iOS 平台无 Application 概念，androidContext 保持 null。
 * 平台能力通过 KMP 预期/实际机制或平台特定 API 提供。
 */
actual object AppContext {
    actual var androidContext: Any? = null
}
