package com.kuikly.init.platform

import com.tencent.tmm.knoi.initialize

/**
 * OHOS Native 桥接入口
 *
 * 提供 C 可调用的顶层函数，供 napi_init.cpp 调用。
 * 通过 Kotlin/Native 的 C 互操作机制暴露给 libshared.so。
 */

/**
 * 初始化 OHOS 平台（Koin + InitTask + KNOI Bridge）
 *
 * 由 napi_init.cpp 在 initKuikly() 之后调用。
 * 导出为 C 函数：com_kuikly_init_platform_setupOHOSPlatform
 */
@Suppress("unused")
fun setupOHOSPlatform() {
    setupOHKoin()
    // 调用 KNOI 桥接函数，防止 DCE 消除 com_tencent_tmm_knoi_initBridge 符号。
    // KNOI 运行时通过 dlsym 动态查找该符号，若被消除会导致运行时崩溃。
    // initialize() 内部调用 initBase() 注册服务代理，重复调用幂等。
    initialize()
}
