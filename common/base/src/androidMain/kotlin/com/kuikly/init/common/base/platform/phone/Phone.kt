package com.kuikly.init.common.base.platform.phone

import android.content.Intent
import android.net.Uri
import com.kuikly.init.common.base.platform.AppContext

/**
 * Android 电话拨打实现
 *
 * 基于 Intent.ACTION_DIAL 跳转拨号界面，无需 CALL_PHONE 权限。
 * 不会直接拨打电话，用户需手动确认。
 */
actual class Phone {

    actual fun call(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            AppContext.application.startActivity(intent)
        } catch (e: Exception) {
            // 跳转失败静默处理
        }
    }
}

actual fun providePhone(): Phone = Phone()
