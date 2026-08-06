package com.kuikly.init.common.base.platform

import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * 权限请求结果桥接器
 *
 * 用于在 KMP 模块中通过 ActivityCompat.requestPermissions 申请权限并获取结果。
 * 宿主 Activity 必须在 onRequestPermissionsResult 中调用 handleResult()。
 */
object PermissionResultBridge {

    private val requestCodeGenerator = AtomicInteger(2000)

    /** requestCode -> 结果回调 */
    private val callbacks = mutableMapOf<Int, (permissions: Array<String>, grantResults: IntArray) -> Unit>()

    /**
     * 注册一个权限请求结果回调，返回请求码
     */
    fun register(callback: (permissions: Array<String>, grantResults: IntArray) -> Unit): Int {
        val requestCode = requestCodeGenerator.incrementAndGet()
        callbacks[requestCode] = callback
        return requestCode
    }

    /**
     * 处理权限请求结果（由宿主 Activity 在 onRequestPermissionsResult 中调用）
     */
    fun handleResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        callbacks.remove(requestCode)?.invoke(permissions, grantResults)
    }

    /**
     * 申请权限并挂起等待结果
     *
     * @param activity 宿主 Activity
     * @param permissions 权限列表
     * @return 是否全部授权
     */
    suspend fun requestPermissions(
        activity: android.app.Activity,
        permissions: Array<String>
    ): Boolean = suspendCancellableCoroutine { continuation ->
        try {
            val requestCode = register { _, grantResults ->
                if (continuation.isActive) {
                    val allGranted = grantResults.isNotEmpty() &&
                        grantResults.all { it == android.content.pm.PackageManager.PERMISSION_GRANTED }
                    continuation.resume(allGranted)
                }
            }
            androidx.core.app.ActivityCompat.requestPermissions(activity, permissions, requestCode)
        } catch (e: Exception) {
            if (continuation.isActive) {
                continuation.resume(false)
            }
        }
    }
}
