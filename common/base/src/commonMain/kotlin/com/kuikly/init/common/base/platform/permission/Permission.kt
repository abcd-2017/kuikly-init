package com.kuikly.init.common.base.platform.permission

/** 权限状态 */
enum class PermissionStatus { GRANTED, DENIED, NOT_DETERMINED }

/**
 * 权限请求能力抽象
 *
 * 提供权限状态检查和申请功能。
 * - Android: 基于 ContextCompat / ActivityCompat
 * - iOS: 基于各框架原生 API
 * - OHOS: 基于 abilityAccessCtrl
 */
expect class Permission {
    /** 检查权限状态 */
    fun checkPermission(permission: String): PermissionStatus

    /** 请求权限（callback 返回 "GRANTED" / "DENIED"） */
    fun requestPermission(permission: String, callback: (String) -> Unit)
}

/** 全局访问入口 */
expect fun providePermission(): Permission
