package com.kuikly.init.common.base.platform.time

import java.util.Date
import java.util.TimeZone

/**
 * Android 时区信息实现
 *
 * 基于 java.util.TimeZone，rawOffset 返回毫秒，转换为分钟/小时。
 */
actual class Timezone {
    private val timeZone: TimeZone = TimeZone.getDefault()
    private val now = Date()

    actual fun getTimezoneId(): String = timeZone.id

    actual fun getOffsetMinutes(): Int = timeZone.rawOffset / 60000

    actual fun getOffsetHours(): Float = timeZone.rawOffset / 3600000f

    actual fun isDaylightSaving(): Boolean = timeZone.inDaylightTime(now)

    actual fun getAbbreviation(): String = timeZone.getDisplayName(
        timeZone.inDaylightTime(now),
        TimeZone.SHORT
    )
}

actual fun provideTimezone(): Timezone = Timezone()
