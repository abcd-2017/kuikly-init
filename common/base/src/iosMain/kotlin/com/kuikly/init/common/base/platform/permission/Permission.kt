package com.kuikly.init.common.base.platform.permission

import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

/**
 * iOS 权限实现
 *
 * iOS 没有统一的权限检查 API，各权限框架（Camera/Location等）各有独立接口。
 * 当前提供基础实现：
 * - checkPermission: 仅通过 canOpenURL 检查设置页可访问性作为兜底
 * - requestPermission: 打开系统设置页（无法直接申请特定权限）
 *
 * TODO: 根据实际权限类型，对接 AVFoundation / CoreLocation 等框架原生 API
 */
actual class Permission {

    actual fun checkPermission(permission: String): PermissionStatus {
        return try {
            // iOS 无统一权限检查 API，返回 NOT_DETERMINED
            // 业务层应根据具体权限类型调用对应框架 API
            PermissionStatus.NOT_DETERMINED
        } catch (e: Exception) {
            PermissionStatus.NOT_DETERMINED
        }
    }

    actual fun requestPermission(permission: String, callback: (String) -> Unit) {
        try {
            // iOS 无法直接弹出系统权限申请（首次调用时各框架会自动触发）
            // 此实现仅打开系统设置页，引导用户手动开启
            NSOperationQueue.mainQueue.addOperationWithBlock {
                val settingsUrl = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
                if (settingsUrl != null && UIApplication.sharedApplication.canOpenURL(settingsUrl)) {
                    UIApplication.sharedApplication.openURL(settingsUrl)
                }
            }
            // 打开设置页不等于授权成功
            callback("DENIED")
        } catch (e: Exception) {
            callback("DENIED")
        }
    }
}

actual fun providePermission(): Permission = Permission()
