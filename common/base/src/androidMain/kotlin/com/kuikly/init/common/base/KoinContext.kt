package com.kuikly.init.common.base

import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

/**
 * Android 平台 KoinContext 实际实现
 */
actual object KoinContext {

    actual inline fun <reified T : Any> get(
        qualifier: Qualifier?,
        noinline parameters: ParametersDefinition?
    ): T {
        val koin = org.koin.core.context.GlobalContext.get()
        return koin.get(T::class, qualifier, parameters)
    }

    actual fun <T : Any> get(
        clazz: kotlin.reflect.KClass<*>,
        qualifier: Any?,
        parameters: (() -> Any)?
    ): T {
        val koin = org.koin.core.context.GlobalContext.get()
        val koinQualifier = qualifier as? Qualifier
        val koinParams = parameters?.let { ParametersDefinition(it.invoke()) }
        return koin.get(clazz, koinQualifier, koinParams)
    }
}
