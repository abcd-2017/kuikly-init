package com.kuikly.init.common.base.platform.dialog

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import com.kuikly.init.common.base.platform.AppContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


/**
 * Android 对话框实现
 *
 * 基于 AlertDialog.Builder，通过 Handler(Looper.getMainLooper()) 确保主线程。
 * 异步方法通过 callback 返回结果，内部使用 suspendCancellableCoroutine 桥接。
 */
actual class Dialog(private val context: Context) {

    private val mainHandler = Handler(Looper.getMainLooper())

    actual fun showAlert(title: String, message: String, confirmText: String) {
        mainHandler.post {
            try {
                AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(confirmText) { dialog, _ -> dialog.dismiss() }
                    .show()
            } catch (e: Exception) {
                // 对话框显示失败静默处理
            }
        }
    }

    actual fun showConfirm(
        title: String,
        message: String,
        confirmText: String,
        cancelText: String,
        callback: (Int) -> Unit
    ) {
        runBlocking {
            val result = suspendCancellableCoroutine { continuation ->
                mainHandler.post {
                    try {
                        AlertDialog.Builder(context)
                            .setTitle(title)
                            .setMessage(message)
                            .setPositiveButton(confirmText) { dialog, _ ->
                                dialog.dismiss()
                                continuation.resume(0)
                            }
                            .setNegativeButton(cancelText) { dialog, _ ->
                                dialog.dismiss()
                                continuation.resume(1)
                            }
                            .show()
                    } catch (e: Exception) {
                        continuation.resume(-1)
                    }
                }
            }
            callback(result)
        }
    }

    actual fun showActionSheet(
        title: String?,
        message: String?,
        options: List<String>,
        callback: (Int) -> Unit
    ) {
        runBlocking {
            val result = suspendCancellableCoroutine { continuation ->
                mainHandler.post {
                    try {
                        val items = options.toTypedArray()
                        AlertDialog.Builder(context)
                            .apply { title?.let { setTitle(it) } }
                            .apply { message?.let { setMessage(it) } }
                            .setItems(items) { dialog, which ->
                                dialog.dismiss()
                                continuation.resume(which)
                            }
                            .setOnCancelListener {
                                continuation.resume(-1)
                            }
                            .show()
                    } catch (e: Exception) {
                        continuation.resume(-1)
                    }
                }
            }
            callback(result)
        }
    }
}

actual fun provideDialog(): Dialog = Dialog(AppContext.application)
