package com.kuikly.init.common.base.platform.scan

import android.app.Activity
import android.content.Intent
import com.kuikly.init.common.base.platform.ActivityResultBridge
import com.kuikly.init.common.base.platform.AppContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Android 扫码实现
 *
 * 基于 ZXing 扫码应用的 Intent 跳转（com.google.zxing.client.android.SCAN）。
 * 通过 ActivityResultBridge 获取扫描结果。
 *
 * 注意：
 * - 需要设备安装 ZXing 扫码应用（Barcode Scanner）或兼容应用。
 * - 若未安装，会返回 null，建议业务层引导用户安装。
 * - 完整集成 ML Kit BarcodeScanning 需额外依赖和自定义扫描 Activity。
 */
actual class Scanner {

    actual fun startScan(callback: (ScanResult?) -> Unit) {
        runBlocking {
            try {
                val activity = AppContext.currentActivity
                if (activity == null) {
                    callback(null)
                    return@runBlocking
                }

                val result = suspendCancellableCoroutine { continuation ->
                    val intent = Intent("com.google.zxing.client.android.SCAN").apply {
                        putExtra("SCAN_MODE", "QR_CODE_MODE")
                        putExtra("PROMPT_MESSAGE", "将二维码放入框内自动扫描")
                    }

                    val requestCode = ActivityResultBridge.register { resultCode, data ->
                        if (continuation.isActive) {
                            if (resultCode == Activity.RESULT_OK) {
                                val content = data?.getStringExtra("SCAN_RESULT")
                                val format = data?.getStringExtra("SCAN_RESULT_FORMAT") ?: "UNKNOWN"
                                if (content != null) {
                                    continuation.resume(ScanResult(content = content, format = format))
                                } else {
                                    continuation.resume(null)
                                }
                            } else {
                                // 用户取消或扫描失败
                                continuation.resume(null)
                            }
                        }
                    }

                    activity.startActivityForResult(intent, requestCode)
                }
                callback(result)
            } catch (e: Exception) {
                callback(null)
            }
        }
    }
}

actual fun provideScanner(): Scanner = Scanner()
