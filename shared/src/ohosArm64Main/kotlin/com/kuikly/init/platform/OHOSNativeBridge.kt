package com.kuikly.init.platform

/**
 * OHOS Native 桥接入口
 *
 * 提供 C 可调用的顶层函数，供 napi_init.cpp 调用。
 * 通过 Kotlin/Native 的 C 互操作机制暴露给 libshared.so。
 */

/**
 * 初始化 OHOS 平台（Koin + InitTask）
 *
 * 由 napi_init.cpp 在 initKuikly() 之后调用。
 * 导出为 C 函数：com_kuikly_init_platform_setupOHOSPlatform
 */
@Suppress("unused")
fun setupOHOSPlatform() {
    setupOHKoin()
}
