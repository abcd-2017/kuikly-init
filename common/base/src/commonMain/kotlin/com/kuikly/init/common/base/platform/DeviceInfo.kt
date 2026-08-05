package com.kuikly.init.common.base.platform

/**
 * 设备信息抽象
 */
expect class DeviceInfo {
    /** 设备唯一标识 */
    fun getDeviceId(): String

    /** 操作系统版本 */
    fun getOSVersion(): String

    /** 设备型号 */
    fun getDeviceModel(): String
}
