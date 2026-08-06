package com.kuikly.init.common.base.platform.battery

import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceBatteryState

/**
 * iOS 电池状态实现
 *
 * 基于 UIDevice.batteryLevel / batteryState，需先开启 batteryMonitoringEnabled。
 */
actual class Battery {
    actual fun getBatteryInfo(): BatteryInfo {
        return try {
            UIDevice.currentDevice.batteryMonitoringEnabled = true
            val level = (UIDevice.currentDevice.batteryLevel * 100).toInt()
            val isCharging = UIDevice.currentDevice.batteryState == UIDeviceBatteryState.UIDeviceBatteryStateCharging
            BatteryInfo(level = level, isCharging = isCharging, isLowBattery = level in 1..20)
        } catch (e: Exception) {
            BatteryInfo(level = -1, isCharging = false, isLowBattery = false)
        }
    }

    actual fun isCharging(): Boolean = getBatteryInfo().isCharging

    actual fun getLevel(): Int = getBatteryInfo().level
}

actual fun provideBattery(): Battery = Battery()
