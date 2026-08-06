package com.kuikly.init.common.base.platform.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.kuikly.init.common.base.platform.AppContext
import com.kuikly.init.common.base.platform.PermissionResultBridge
import kotlinx.coroutines.runBlocking

/**
 * Android 权限实现
 *
 * checkPermission 基于 ContextCompat.checkSelfPermission。
 * requestPermission 基于 ActivityCompat.requestPermissions，通过 PermissionResultBridge 获取结果。
 *
 * 注意：
 * - requestPermission 需要宿主 Activity 在 onRequestPermissionsResult 中调用
 *   PermissionResultBridge.handleResult()。
 * - 若当前无 Activity 上下文，callback 返回 "DENIED"。
 */
actual class Permission(private val context: Context) {

    actual fun checkPermission(permission: String): PermissionStatus {
        return try {
            val result = ContextCompat.checkSelfPermission(context, permission)
            if (result == PackageManager.PERMISSION_GRANTED) PermissionStatus.GRANTED
            else PermissionStatus.DENIED
        } catch (e: Exception) {
            PermissionStatus.NOT_DETERMINED
        }
    }

    actual fun requestPermission(permission: String, callback: (String) -> Unit) {
        runBlocking {
            try {
                val activity = AppContext.currentActivity
                if (activity == null) {
                    callback("DENIED")
                    return@runBlocking
                }
                val granted = PermissionResultBridge.requestPermissions(activity, arrayOf(permission))
                callback(if (granted) "GRANTED" else "DENIED")
            } catch (e: Exception) {
                callback("DENIED")
            }
        }
    }
}

actual fun providePermission(): Permission = Permission(AppContext.application)
