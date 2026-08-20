package com.tencent.tmm.knoi

import com.tencent.tmm.knoi.modules.initBase
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.CName
import kotlinx.cinterop.ExperimentalForeignApi
import platform.ohos.napi_env
import platform.ohos.napi_value

@OptIn(ExperimentalNativeApi::class)
@CName(externName = "com_tencent_tmm_knoi_initBridge")
public fun initBridge() {
    println("[KnoiBridge] initBridge start")
    initBase()
    println("[KnoiBridge] initBridge done")
}

@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
@CName(externName = "com_tencent_tmm_knoi_initEnv")
public fun initEnvExport(
    env: napi_env,
    `value`: napi_value,
    debug: Boolean,
) {
    println("[KnoiBridge] initEnvExport start")
    preInitEnv(env, debug)
    InitEnv(env, value, debug)
    println("[KnoiBridge] initEnvExport done")
}
