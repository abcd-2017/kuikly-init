package com.kuikly.init.common.base.platform

import android.app.Activity
import android.content.Intent
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Activity 结果桥接器
 *
 * 用于在 KMP 模块中通过 startActivityForResult 启动 Activity 并获取结果。
 * 宿主 Activity 必须在 onActivityResult 中调用 handleResult()。
 *
 * 使用方式：
 * ```
 * val requestCode = ActivityResultBridge.register { resultCode, data ->
 *     // 处理结果
 * }
 * activity.startActivityForResult(intent, requestCode)
 * ```
 */
object ActivityResultBridge {

    private val requestCodeGenerator = AtomicInteger(1000)

    /** requestCode -> 结果回调 */
    private val callbacks = mutableMapOf<Int, (resultCode: Int, data: Intent?) -> Unit>()

    /**
     * 注册一个 Activity 结果回调，返回请求码
     *
     * @param callback 结果回调，参数为 (resultCode, data)
     * @return 请求码，传给 startActivityForResult
     */
    fun register(callback: (resultCode: Int, data: Intent?) -> Unit): Int {
        val requestCode = requestCodeGenerator.incrementAndGet()
        callbacks[requestCode] = callback
        return requestCode
    }

    /**
     * 处理 Activity 结果（由宿主 Activity 在 onActivityResult 中调用）
     *
     * @param requestCode 请求码
     * @param resultCode 结果码
     * @param data 返回数据
     */
    fun handleResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbacks.remove(requestCode)?.invoke(resultCode, data)
    }

    /**
     * 启动 Activity 并挂起等待结果（便捷方法）
     *
     * @param activity 宿主 Activity
     * @param intent 启动意图
     * @return Pair<resultCode, data>，异常返回 RESULT_CANCELED
     */
    suspend fun startActivityForResult(
        activity: Activity,
        intent: Intent
    ): Pair<Int, Intent?> = suspendCancellableCoroutine { continuation ->
        try {
            val requestCode = register { resultCode, data ->
                if (continuation.isActive) {
                    continuation.resume(resultCode to data)
                }
            }
            activity.startActivityForResult(intent, requestCode)
        } catch (e: Exception) {
            if (continuation.isActive) {
                continuation.resume(Activity.RESULT_CANCELED to null)
            }
        }
    }
}
