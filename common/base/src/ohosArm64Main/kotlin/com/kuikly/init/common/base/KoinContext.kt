package com.kuikly.init.common.base

/**
 * OHOS 平台的 KoinContext 桩实现
 */
actual object KoinContext {

    actual inline fun <reified T : Any> get(
        qualifier: Any?,
        noinline parameters: (() -> Any)?
    ): T {
        throw UnsupportedOperationException("Koin is not supported on OHOS platform")
    }

    actual fun <T : Any> get(
        clazz: kotlin.reflect.KClass<*>,
        qualifier: Any?,
        parameters: (() -> Any)?
    ): T {
        throw UnsupportedOperationException("Koin is not supported on OHOS platform")
    }
}
