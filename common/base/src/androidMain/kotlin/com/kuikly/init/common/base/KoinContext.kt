package com.kuikly.init.common.base

import org.koin.core.qualifier.Qualifier

/**
 * Android 平台 KoinContext 实际实现
 */
actual object KoinContext {

    actual inline fun <reified T : Any> get(
        qualifier: Any?,
        noinline parameters: (() -> Any)?
    ): T {
        val koin = org.koin.core.context.GlobalContext.get()
        val koinQualifier = qualifier as? Qualifier
        @Suppress("UNCHECKED_CAST")
        return koin.get(T::class, koinQualifier) as T
    }

    actual fun <T : Any> get(
        clazz: kotlin.reflect.KClass<*>,
        qualifier: Any?,
        parameters: (() -> Any)?
    ): T {
        val koin = org.koin.core.context.GlobalContext.get()
        val koinQualifier = qualifier as? Qualifier
        @Suppress("UNCHECKED_CAST")
        return koin.get(clazz, koinQualifier) as T
    }
}
