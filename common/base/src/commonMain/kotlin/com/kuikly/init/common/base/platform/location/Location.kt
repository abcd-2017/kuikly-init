package com.kuikly.init.common.base.platform.location

/**
 * 地理位置信息
 *
 * @param latitude 纬度
 * @param longitude 经度
 * @param altitude 海拔（米），可能为 null
 * @param accuracy 精度（米），可能为 null
 * @param speed 速度（米/秒），可能为 null
 * @param timestamp 时间戳（毫秒），可能为 null
 */
data class Location(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double? = null,
    val accuracy: Float? = null,
    val speed: Float? = null,
    val timestamp: Long? = null
)

/** 定位精度等级 */
enum class LocationAccuracy { COARSE, BALANCED, PRECISE }

/**
 * 地理位置能力抽象
 *
 * 提供权限申请和当前位置获取功能。
 * - Android: 基于 FusedLocationProviderClient
 * - iOS: 基于 CoreLocation.CLLocationManager
 * - OHOS: 基于 @ohos.geoLocationManager
 */
expect class LocationProvider {
    /** 请求定位权限（callback 返回是否授权） */
    fun requestPermission(callback: (Boolean) -> Unit)

    /** 获取当前位置（callback 返回位置信息，可能为 null） */
    fun getCurrentLocation(accuracy: LocationAccuracy, callback: (Location?) -> Unit)
}

/** 全局访问入口 */
expect fun provideLocationProvider(): LocationProvider
