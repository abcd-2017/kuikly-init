package com.kuikly.init.common.base.platform.time

import platform.Foundation.NSTimeZone

/**
 * iOS 时区信息实现
 *
 * 基于 NSTimeZone.systemTimeZone，secondsFromGMT 返回秒，需 * 60 转分钟。
 */
actual class Timezone {
    private val timeZone: NSTimeZone = NSTimeZone.systemTimeZone

    actual fun getTimezoneId(): String = timeZone.name

    actual fun getOffsetMinutes(): Int = (timeZone.secondsFromGMT / 60).toInt()

    actual fun getOffsetHours(): Float = timeZone.secondsFromGMT / 3600f

    actual fun isDaylightSaving(): Boolean = timeZone.isDaylightSavingTime()

    actual fun getAbbreviation(): String = timeZone.abbreviation ?: "UNK"
}

actual fun provideTimezone(): Timezone = Timezone()
