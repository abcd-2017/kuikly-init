package com.kuikly.init.platform

/**
 * OHOS 平台初始化入口 (OHOS does not use Koin)
 *
 * 由 napi_init.cpp 在 initKuikly() 之后调用。
 */
fun setupOHKoin() {
    // OHOS 不使用 Koin DI，初始化逻辑由 ETS 侧完成
    println("[OHOSInitSetup] OHOS platform initialized (no Koin)")
}
