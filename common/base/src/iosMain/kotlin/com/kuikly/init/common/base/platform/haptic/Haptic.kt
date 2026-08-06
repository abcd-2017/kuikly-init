package com.kuikly.init.common.base.platform.haptic

import platform.Foundation.NSOperationQueue
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType

/**
 * iOS 震动反馈实现
 *
 * 基于 UIImpactFeedbackGenerator 和 UINotificationFeedbackGenerator。
 * 所有 UI 操作通过 NSOperationQueue.mainQueue 调度到主线程执行。
 */
actual class Haptic {

    actual fun impact(style: HapticStyle) {
        NSOperationQueue.mainQueue.addOperationWithBlock {
            try {
                val feedbackStyle = when (style) {
                    HapticStyle.LIGHT -> UIImpactFeedbackStyle.UIImpactFeedbackStyleLight
                    HapticStyle.MEDIUM -> UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium
                    HapticStyle.HEAVY -> UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy
                }
                val generator = UIImpactFeedbackGenerator(feedbackStyle)
                generator.prepare()
                generator.impactOccurred()
            } catch (e: Exception) {
                // 震动失败静默处理
            }
        }
    }

    actual fun notification(type: HapticNotification) {
        NSOperationQueue.mainQueue.addOperationWithBlock {
            try {
                val feedbackType = when (type) {
                    HapticNotification.SUCCESS -> UINotificationFeedbackType.UINotificationFeedbackTypeSuccess
                    HapticNotification.WARNING -> UINotificationFeedbackType.UINotificationFeedbackTypeWarning
                    HapticNotification.FAILURE -> UINotificationFeedbackType.UINotificationFeedbackTypeError
                }
                val generator = UINotificationFeedbackGenerator()
                generator.prepare()
                generator.notificationOccurred(feedbackType)
            } catch (e: Exception) {
                // 震动失败静默处理
            }
        }
    }

    actual fun selectionChanged() {
        NSOperationQueue.mainQueue.addOperationWithBlock {
            try {
                val generator = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)
                generator.prepare()
                generator.selectionChanged()
            } catch (e: Exception) {
                // 震动失败静默处理
            }
        }
    }

    actual fun stop() {
        // iOS 震动反馈不支持主动停止（持续时间极短）
    }
}

actual fun provideHaptic(): Haptic = Haptic()
