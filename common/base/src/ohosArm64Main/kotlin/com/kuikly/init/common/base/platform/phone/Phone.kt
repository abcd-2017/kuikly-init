package com.kuikly.init.common.base.platform.phone

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

/**
 * OHOS 电话拨打实现（通过 KNOI 调用 ETS 侧）
 *
 * 基于 @kit.TelephonyKit call.makeCall()，由 ArkTS 侧实现具体逻辑。
 */
actual class Phone {

    private val service get() = getIOHOSPlatformServiceApi()

    actual fun call(phoneNumber: String) {
        try {
            service?.callPhone(phoneNumber)
        } catch (e: Exception) {
            // 跳转失败静默处理
        }
    }
}

actual fun providePhone(): Phone = Phone()
