package com.kuikly.init.common.base.platform.time

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * Android NTP 时间同步实现
 *
 * 使用标准 NTP 协议（UDP）请求 NTP 服务器，缓存时钟偏差。
 * 所有网络请求在 IO 线程执行，通过 Mutex 保证 offset 线程安全。
 */
actual class NtpClock {
    private val mutex = Mutex()
    private var cachedOffset: Long? = null

    actual suspend fun getServerTime(ntpServer: String): Long? = withContext(Dispatchers.IO) {
        try {
            val offset = requestNtpOffset(ntpServer) ?: return@withContext null
            val serverTime = System.currentTimeMillis() + offset
            mutex.withLock { cachedOffset = offset }
            serverTime
        } catch (e: Exception) {
            null
        }
    }

    actual suspend fun getClockOffset(ntpServer: String): Long? = withContext(Dispatchers.IO) {
        val cached = mutex.withLock { cachedOffset }
        if (cached != null) return@withContext cached
        // 网络请求在锁外执行，避免阻塞其他协程
        val offset = requestNtpOffset(ntpServer)
        if (offset != null) {
            mutex.withLock { cachedOffset = offset }
        }
        offset
    }

    actual suspend fun getAdjustedTime(): Long = mutex.withLock {
        val offset = cachedOffset
        if (offset != null) {
            System.currentTimeMillis() + offset
        } else {
            System.currentTimeMillis()
        }
    }

    private fun requestNtpOffset(ntpServer: String): Long? {
        return try {
            val socket = DatagramSocket()
            socket.soTimeout = 5000
            val address = InetAddress.getByName(ntpServer)

            // NTP 请求报文（48 字节）
            val buffer = ByteArray(48)
            buffer[0] = 0x1B // LI=0, VN=3, Mode=3 (client)

            val request = DatagramPacket(buffer, buffer.size, address, 123)
            socket.send(request)

            val response = DatagramPacket(buffer, buffer.size)
            socket.receive(response)
            socket.close()

            // 解析 Transmit Timestamp（第 40-43 字节，整数部分）
            var seconds = 0L
            for (i in 40..43) {
                seconds = (seconds shl 8) or (buffer[i].toLong() and 0xFF)
            }
            // NTP 时间从 1900-01-01 开始，Unix 时间从 1970-01-01 开始，差值 2208988800 秒
            val ntpEpochOffset = 2208988800L
            if (seconds == 0L) return null
            (seconds - ntpEpochOffset) * 1000 - System.currentTimeMillis()
        } catch (e: Exception) {
            null
        }
    }
}

actual fun provideNtpClock(): NtpClock = NtpClock()
