package com.kuikly.init.platform

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.CName

/**
 * OHOS Native 桥接入口
 *
 * 提供 C 可调用的顶层函数，供 napi_init.cpp 调用。
 * 通过 Kotlin/Native 的 C 互操作机制暴露给 libshared.so。
 */

/**
 * 桥接初始化符号导出（空实现，仅用于导出符号）
 *
 * KNOI 运行时通过 dlsym 动态查找 com_tencent_tmm_knoi_initBridge 符号。
 * 该函数在 shared 模块中直接定义并导出，确保符号必定存在于 libshared.so 的动态符号表中。
 *
 * 注意：此函数仅用于符号导出，不涉及任何框架 API 调用。
 * 实际初始化逻辑由 KNOI 框架侧自动处理（通过 libknoi.so 内部的符号）。
 */
@Suppress("REDUNDANT_PROJECTION")
@OptIn(ExperimentalNativeApi::class)
@CName("com_tencent_tmm_knoi_initBridge")
fun exportInitBridge() {
    // 空实现：此函数仅用于导出 C 符号，供 KNOI 运行时 dlsym 查找
}

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
