package com.kuikly.init.common.base.platform.time

/**
 * NTP 时间同步抽象
 *
 * 通过 NTP 协议获取远程服务器时间，计算本地时钟偏差，
 * 提供校准后的时间戳。所有返回值为毫秒。
 */
expect class NtpClock {
    /**
     * 获取服务器时间戳（毫秒）
     * @param ntpServer NTP 服务器地址，默认 pool.ntp.org
     * @return 服务器时间戳（毫秒），失败返回 null
     */
    suspend fun getServerTime(ntpServer: String = "pool.ntp.org"): Long?

    /**
     * 获取本地时间与服务器时间的偏差（毫秒）
     * @param ntpServer NTP 服务器地址，默认 pool.ntp.org
     * @return 偏差值（毫秒），失败返回 null
     */
    suspend fun getClockOffset(ntpServer: String = "pool.ntp.org"): Long?

    /**
     * 获取校准后的当前时间（毫秒）
     * 若无缓存的 offset，回退到 System.currentTimeMillis()
     */
    suspend fun getAdjustedTime(): Long
}

/** 全局访问入口 */
expect fun provideNtpClock(): NtpClock
