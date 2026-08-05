package com.kuikly.init.platform

/**
 * OHOS 平台初始化入口 (OHOS does not use Koin)
 *
 * KNOI 服务注册已移到 ETS 侧（EntryAbility.ets），此函数保留为空以避免破坏 C 桥接调用。
 */
fun setupOHKoin() {
    // KNOI 服务注册已移至 ETS 侧
    println("[OHOSInitSetup] OHOS platform initialized (KNOI services registered on ETS side)")
}
