package com.kuikly.init.common.base.platform.time

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * OHOS NTP 时间同步实现（通过 KNOI 调用 ETS 侧）
 *
 * ETS 侧使用 @ohos.net.socket UDPSocket 实现 NTP 客户端。
 */
actual class NtpClock {
    private val mutex = Mutex()
    private var cachedOffset: Long? = null
    private val service get() = getIOHOSPlatformServiceApi()

    actual suspend fun getServerTime(ntpServer: String): Long? = mutex.withLock {
        try {
            val serverTime = service?.getServerTime(ntpServer)
            if (serverTime != null && serverTime > 0) {
                val offset = serverTime - kotlin.system.getTimeMillis()
                cachedOffset = offset
                serverTime
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    actual suspend fun getClockOffset(ntpServer: String): Long? = mutex.withLock {
        cachedOffset ?: run {
            try {
                val offset = service?.getClockOffset(ntpServer)
                if (offset != null) {
                    cachedOffset = offset
                    offset
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    actual suspend fun getAdjustedTime(): Long = mutex.withLock {
        val offset = cachedOffset
        if (offset != null) {
            kotlin.system.getTimeMillis() + offset
        } else {
            kotlin.system.getTimeMillis()
        }
    }
}

actual fun provideNtpClock(): NtpClock = NtpClock()
