package com.kuikly.init.common.base.platform.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.kuikly.init.common.base.platform.AppContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Android 地理位置实现
 *
 * 基于 LocationManager（Android 框架 API，无需额外依赖）。
 * 注意：未使用 FusedLocationProviderClient（需 play-services-location），
 * 如需更高精度可后续替换。
 */
actual class LocationProvider(private val context: Context) {

    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    actual fun requestPermission(callback: (Boolean) -> Unit) {
        val hasPermission = try {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) { false }
        // Android 需通过 Activity 申请权限，此处仅返回当前状态
        callback(hasPermission)
    }

    actual fun getCurrentLocation(accuracy: LocationAccuracy, callback: (Location?) -> Unit) {
        runBlocking {
            // 先检查权限
            val hasPermission = try {
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
            } catch (e: Exception) { false }

            if (!hasPermission) {
                callback(null)
                return@runBlocking
            }

            val result = suspendCancellableCoroutine { continuation ->
                try {
                    val provider = when (accuracy) {
                        LocationAccuracy.COARSE -> LocationManager.NETWORK_PROVIDER
                        else -> LocationManager.GPS_PROVIDER
                    }

                    // 先尝试获取最后已知位置
                    val lastKnown = try {
                        locationManager.getLastKnownLocation(provider)
                            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                            ?: locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    } catch (e: Exception) {
                        null
                    }

                    if (lastKnown != null) {
                        continuation.resume(
                            Location(
                                latitude = lastKnown.latitude,
                                longitude = lastKnown.longitude,
                                altitude = if (lastKnown.hasAltitude()) lastKnown.altitude else null,
                                accuracy = if (lastKnown.hasAccuracy()) lastKnown.accuracy else null,
                                speed = if (lastKnown.hasSpeed()) lastKnown.speed else null,
                                timestamp = lastKnown.time
                            )
                        )
                        return@suspendCancellableCoroutine
                    }

                    // 无最后已知位置，请求单次更新
                    val listener = object : android.location.LocationListener {
                        override fun onLocationChanged(location: android.location.Location) {
                            locationManager.removeUpdates(this)
                            if (continuation.isActive) {
                                continuation.resume(
                                    Location(
                                        latitude = location.latitude,
                                        longitude = location.longitude,
                                        altitude = if (location.hasAltitude()) location.altitude else null,
                                        accuracy = if (location.hasAccuracy()) location.accuracy else null,
                                        speed = if (location.hasSpeed()) location.speed else null,
                                        timestamp = location.time
                                    )
                                )
                            }
                        }

                        @Deprecated("Deprecated in API 29")
                        override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
                        override fun onProviderEnabled(provider: String) {}
                        override fun onProviderDisabled(provider: String) {}
                    }

                    try {
                        locationManager.requestLocationUpdates(provider, 0L, 0f, listener, Looper.getMainLooper())
                    } catch (e: Exception) {
                        continuation.resume(null)
                        return@suspendCancellableCoroutine
                    }

                    // 超时兜底：10 秒后取消
                    val handler = android.os.Handler(Looper.getMainLooper())
                    val timeoutRunnable = Runnable {
                        if (continuation.isActive) {
                            locationManager.removeUpdates(listener)
                            continuation.resume(null)
                        }
                    }
                    handler.postDelayed(timeoutRunnable, 10000)

                    continuation.invokeOnCancellation {
                        locationManager.removeUpdates(listener)
                        handler.removeCallbacks(timeoutRunnable)
                    }
                } catch (e: Exception) {
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }
            }
            callback(result)
        }
    }
}

actual fun provideLocationProvider(): LocationProvider = LocationProvider(AppContext.application)
