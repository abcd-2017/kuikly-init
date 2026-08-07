package com.kuikly.init.common.base.platform.permission

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

/**
 * OHOS 权限实现（通过 KNOI 调用 ETS 侧）
 *
 * 基于 abilityAccessCtrl，由 ArkTS 侧实现具体逻辑。
 */
actual class Permission {

    private val service get() = getIOHOSPlatformServiceApi()

    actual fun checkPermission(permission: String): PermissionStatus {
        return try {
            // OHOS 需要 tokenID，此处使用 0 作为默认值
            val result = service?.checkPermissionSync(0L, permission)
            when (result) {
                "GRANTED" -> PermissionStatus.GRANTED
                "DENIED" -> PermissionStatus.DENIED
                else -> PermissionStatus.NOT_DETERMINED
            }
        } catch (e: Exception) {
            PermissionStatus.NOT_DETERMINED
        }
    }

    actual fun requestPermission(permission: String, callback: (String) -> Unit) {
        try {
            service?.requestPermissions(listOf(permission)) { result ->
                callback(result)
            }
        } catch (e: Exception) {
            callback("DENIED")
        }
    }
}

actual fun providePermission(): Permission = Permission()
