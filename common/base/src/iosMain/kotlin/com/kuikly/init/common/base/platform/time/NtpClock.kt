package com.kuikly.init.common.base.platform.time

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import platform.Foundation.NSDate

/**
 * iOS NTP 时间同步实现（桩实现）
 *
 * TODO: 当前使用 NSDate().timeIntervalSince1970() * 1000 作为回退，
 * 未实现真正的 NTP UDP socket 请求。
 *
 * 完整实现需要：
 * 1. 使用 POSIX socket (sys/socket.h) 建立 UDP 连接
 * 2. 构造 48 字节 NTP 报文（LI=0, VN=3, Mode=3）
 * 3. 发送到 pool.ntp.org:123，设置 5 秒超时
 * 4. 解析返回报文第 40-43 字节（整数部分）
 * 5. NTP 时间从 1900-01-01 起算，需减去 2208988800 秒转 Unix 时间
 *
 * 由于 KMP 中 POSIX socket 跨平台差异大，建议后续评估使用
 * 三方库（如 kotlinx-io）或 native cinterop 方式实现。
 */
actual class NtpClock {
    private val mutex = Mutex()
    private var cachedOffset: Long? = null

    actual suspend fun getServerTime(ntpServer: String): Long? = mutex.withLock {
        try {
            // 桩实现：使用系统时间作为回退
            // TODO: 替换为真实 NTP UDP socket 实现
            val now = (NSDate().timeIntervalSince1970() * 1000).toLong()
            cachedOffset = 0L
            now
        } catch (e: Exception) {
            null
        }
    }

    actual suspend fun getClockOffset(ntpServer: String): Long? = mutex.withLock {
        cachedOffset ?: run {
            try {
                // 桩实现：offset 为 0
                cachedOffset = 0L
                0L
            } catch (e: Exception) {
                null
            }
        }
    }

    actual suspend fun getAdjustedTime(): Long = mutex.withLock {
        val offset = cachedOffset ?: 0L
        (NSDate().timeIntervalSince1970() * 1000).toLong() + offset
    }
}

actual fun provideNtpClock(): NtpClock = NtpClock()
