package com.kuikly.init.common.base.platform.battery

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

/**
 * OHOS 电池状态实现（通过 KNOI 调用 ETS 侧）
 *
 * 基于 @ohos.batteryInfo，由 ArkTS 侧实现具体逻辑。
 */
actual class Battery {
    private val service get() = getIOHOSPlatformServiceApi()

    actual fun getBatteryInfo(): BatteryInfo {
        return try {
            val level = service?.getBatteryLevel() ?: -1
            val isCharging = service?.isCharging() ?: false
            val isLow = service?.isLowBattery() ?: false
            BatteryInfo(level = level, isCharging = isCharging, isLowBattery = isLow)
        } catch (e: Exception) {
            BatteryInfo(level = -1, isCharging = false, isLowBattery = false)
        }
    }

    actual fun isCharging(): Boolean = getBatteryInfo().isCharging

    actual fun getLevel(): Int = getBatteryInfo().level
}

actual fun provideBattery(): Battery = Battery()
