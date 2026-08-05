package com.kuikly.init.common.base.platform.time

/**
 * 时区信息抽象
 *
 * 提供当前设备时区的标识、偏移量、夏令时状态等信息。
 * 所有平台偏移量统一以分钟/小时为单位，便于业务直接使用。
 */
expect class Timezone {
    /** 时区 ID，如 "Asia/Shanghai"、"America/New_York" */
    fun getTimezoneId(): String

    /** 时区偏移量（分钟），如东八区返回 480 */
    fun getOffsetMinutes(): Int

    /** 时区偏移量（小时），如东八区返回 8.0 */
    fun getOffsetHours(): Float

    /** 当前是否处于夏令时 */
    fun isDaylightSaving(): Boolean

    /** 时区缩写，如 "CST"、"PDT" */
    fun getAbbreviation(): String
}

/** 全局访问入口 */
expect fun provideTimezone(): Timezone
