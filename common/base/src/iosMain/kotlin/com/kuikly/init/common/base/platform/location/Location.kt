package com.kuikly.init.common.base.platform.location

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import platform.CoreLocation.CLLocationManager
import platform.Foundation.NSOperationQueue

/**
 * iOS 地理位置实现
 *
 * 基于 CoreLocation.CLLocationManager。
 * 注意：requestPermission() 仅检查当前授权状态，首次调用 startUpdatingLocation 时系统会自动弹出权限申请。
 */
actual class LocationProvider {

    private val locationManager = CLLocationManager()

    actual fun requestPermission(callback: (Boolean) -> Unit) {
        val result = try {
            // iOS 无直接"申请权限"API，首次使用时系统自动弹出
            // 此处仅返回当前授权状态
            val status = CLLocationManager.authorizationStatus()
            when (status) {
                platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways,
                platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse -> true
                else -> false
            }
        } catch (e: Exception) {
            false
        }
        callback(result)
    }

    actual fun getCurrentLocation(accuracy: LocationAccuracy, callback: (Location?) -> Unit) {
        runBlocking {
            val result = try {
                val status = CLLocationManager.authorizationStatus()
                if (status == platform.CoreLocation.kCLAuthorizationStatusDenied ||
                    status == platform.CoreLocation.kCLAuthorizationStatusRestricted) {
                    null
                } else {
                    suspendCancellableCoroutine { continuation ->
                        try {
                            // 设置精度
                            locationManager.desiredAccuracy = when (accuracy) {
                                LocationAccuracy.COARSE -> platform.CoreLocation.kCLLocationAccuracyKilometer
                                LocationAccuracy.BALANCED -> platform.CoreLocation.kCLLocationAccuracyHundredMeters
                                LocationAccuracy.PRECISE -> platform.CoreLocation.kCLLocationAccuracyBest
                            }

                            // 使用 delegate 获取位置并转换为 suspend 函数
                            val delegate = LocationDelegate { location ->
                                if (continuation.isActive) {
                                    continuation.resume(location)
                                }
                            }
                            locationManager.delegate = delegate
                            locationManager.requestLocation()

                            continuation.invokeOnCancellation {
                                locationManager.stopUpdatingLocation()
                            }
                        } catch (e: Exception) {
                            if (continuation.isActive) {
                                continuation.resume(null)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                null
            }
            callback(result)
        }
    }
}

/**
 * CLLocationManagerDelegate 实现
 *
 * 将回调式 API 转换为协程挂起函数。
 */
private class LocationDelegate(
    val onLocation: (Location?) -> Unit
) : platform.CoreLocation.CLLocationManagerDelegateProtocol, platform.Foundation.NSObject() {

    override fun locationManager(
        manager: CLLocationManager,
        didUpdateLocations: List<Any?>
    ) {
        val clLocation = didUpdateLocations.firstOrNull() as? platform.CoreLocation.CLLocation
        if (clLocation != null) {
            onLocation(
                Location(
                    latitude = clLocation.coordinate.latitude,
                    longitude = clLocation.coordinate.longitude,
                    altitude = clLocation.altitude,
                    accuracy = clLocation.horizontalAccuracy.toFloat(),
                    speed = if (clLocation.speed >= 0) clLocation.speed.toFloat() else null,
                    timestamp = (clLocation.timestamp.timeIntervalSince1970 * 1000).toLong()
                )
            )
        } else {
            onLocation(null)
        }
    }

    override fun locationManager(
        manager: CLLocationManager,
        didFailWithError: platform.Foundation.NSError
    ) {
        onLocation(null)
    }
}

actual fun provideLocationProvider(): LocationProvider = LocationProvider()
