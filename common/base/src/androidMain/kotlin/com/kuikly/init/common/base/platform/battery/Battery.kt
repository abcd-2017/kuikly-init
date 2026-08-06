package com.kuikly.init.common.base.platform.battery

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.kuikly.init.common.base.platform.AppContext

/**
 * Android 电池状态实现
 *
 * 基于 BatteryManager，通过 ACTION_BATTERY_CHANGED 广播获取电池信息。
 */
actual class Battery {
    actual fun getBatteryInfo(): BatteryInfo {
        return try {
            val intent = AppContext.application.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val pct = if (level >= 0 && scale > 0) (level * 100 / scale) else -1
            val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
            BatteryInfo(level = pct, isCharging = isCharging, isLowBattery = pct in 1..20)
        } catch (e: Exception) {
            BatteryInfo(level = -1, isCharging = false, isLowBattery = false)
        }
    }

    actual fun isCharging(): Boolean = getBatteryInfo().isCharging

    actual fun getLevel(): Int = getBatteryInfo().level
}

actual fun provideBattery(): Battery = Battery()
