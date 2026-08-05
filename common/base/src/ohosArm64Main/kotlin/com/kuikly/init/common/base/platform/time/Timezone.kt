package com.kuikly.init.common.base.platform.time

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

/**
 * OHOS 时区信息实现（通过 KNOI 调用 ETS 侧）
 */
actual class Timezone {
    private val service get() = getIOHOSPlatformServiceApi()

    actual fun getTimezoneId(): String =
        service?.getTimezoneId() ?: "Asia/Shanghai"

    actual fun getOffsetMinutes(): Int =
        service?.getOffsetMinutes() ?: 480

    actual fun getOffsetHours(): Float =
        service?.getOffsetMinutes()?.div(60f) ?: 8.0f

    actual fun isDaylightSaving(): Boolean =
        service?.isDaylightSaving() ?: false

    actual fun getAbbreviation(): String =
        service?.getTimezoneAbbreviation() ?: "CST"
}

actual fun provideTimezone(): Timezone = Timezone()
