package com.kuikly.init.common.base.platform.battery

/**
 * 电池信息
 *
 * @param level 电量百分比（0-100），未知时为 -1
 * @param isCharging 是否正在充电
 * @param isLowBattery 是否低电量（电量 <= 20%）
 */
data class BatteryInfo(
    val level: Int,
    val isCharging: Boolean,
    val isLowBattery: Boolean = false
)

/**
 * 电池状态能力抽象
 *
 * 提供设备电池电量、充电状态、低电量判断功能。
 */
expect class Battery {
    /** 获取完整电池信息 */
    fun getBatteryInfo(): BatteryInfo

    /** 是否正在充电 */
    fun isCharging(): Boolean

    /** 电量百分比（0-100），未知时为 -1 */
    fun getLevel(): Int
}

/** 全局访问入口 */
expect fun provideBattery(): Battery
