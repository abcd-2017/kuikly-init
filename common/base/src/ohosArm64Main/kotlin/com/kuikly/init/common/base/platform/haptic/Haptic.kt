package com.kuikly.init.common.base.platform.haptic

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

/**
 * OHOS 震动反馈实现（通过 KNOI 调用 ETS 侧）
 *
 * 基于 @ohos.vibrator，由 ArkTS 侧实现具体逻辑。
 */
actual class Haptic {

    private val service get() = getIOHOSPlatformServiceApi()

    actual fun impact(style: HapticStyle) {
        try {
            // style: 0=LIGHT, 1=MEDIUM, 2=HEAVY
            val styleIndex = when (style) {
                HapticStyle.LIGHT -> 2
                HapticStyle.MEDIUM -> 1
                HapticStyle.HEAVY -> 0
            }
            service?.hapticImpact(styleIndex)
        } catch (e: Exception) {
            // 震动失败静默处理
        }
    }

    actual fun notification(type: HapticNotification) {
        try {
            // type: 0=SUCCESS, 1=WARNING, 2=FAILURE
            val typeIndex = when (type) {
                HapticNotification.SUCCESS -> 0
                HapticNotification.WARNING -> 1
                HapticNotification.FAILURE -> 2
            }
            service?.hapticNotification(typeIndex)
        } catch (e: Exception) {
            // 震动失败静默处理
        }
    }

    actual fun selectionChanged() {
        try {
            service?.hapticSelectionChanged()
        } catch (e: Exception) {
            // 震动失败静默处理
        }
    }

    actual fun stop() {
        try {
            service?.hapticStop()
        } catch (e: Exception) {
            // 停止失败静默处理
        }
    }
}

actual fun provideHaptic(): Haptic = Haptic()
