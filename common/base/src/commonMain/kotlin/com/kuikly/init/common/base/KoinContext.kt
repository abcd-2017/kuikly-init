package com.kuikly.init.common.base

/**
 * Koin 容器访问点 (common 期望声明)
 *
 * 各平台提供 actual 实现，OHOS 平台使用桩实现。
 */
expect object KoinContext {

    inline fun <reified T : Any> get(
        qualifier: Any? = null,
        noinline parameters: (() -> Any)? = null
    ): T

    fun <T : Any> get(
        clazz: kotlin.reflect.KClass<*>,
        qualifier: Any? = null,
        parameters: (() -> Any)? = null
    ): T
}
