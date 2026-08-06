package com.kuikly.init.common.base.platform.haptic

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.kuikly.init.common.base.platform.AppContext

/**
 * Android 震动反馈实现
 *
 * API 31+ 使用 VibratorManager，低版本使用 Vibrator。
 * impact 使用预定义 EFFECT，notification 映射到不同振动模式。
 */
actual class Haptic(private val context: Context) {

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    actual fun impact(style: HapticStyle) {
        try {
            if (!vibrator.hasVibrator()) return
            val effect = when (style) {
                HapticStyle.LIGHT -> VibrationEffect.EFFECT_TICK
                HapticStyle.MEDIUM -> VibrationEffect.EFFECT_CLICK
                HapticStyle.HEAVY -> VibrationEffect.EFFECT_HEAVY_CLICK
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(effect))
            }
        } catch (e: Exception) {
            // 震动失败静默处理
        }
    }

    actual fun notification(type: HapticNotification) {
        try {
            if (!vibrator.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val effect = when (type) {
                    HapticNotification.SUCCESS -> VibrationEffect.EFFECT_DOUBLE_CLICK
                    HapticNotification.WARNING -> VibrationEffect.EFFECT_HEAVY_CLICK
                    HapticNotification.FAILURE -> VibrationEffect.EFFECT_DOUBLE_CLICK
                }
                vibrator.vibrate(VibrationEffect.createPredefined(effect))
            }
        } catch (e: Exception) {
            // 震动失败静默处理
        }
    }

    actual fun selectionChanged() {
        try {
            if (!vibrator.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            }
        } catch (e: Exception) {
            // 震动失败静默处理
        }
    }

    actual fun stop() {
        try {
            vibrator.cancel()
        } catch (e: Exception) {
            // 停止失败静默处理
        }
    }
}

actual fun provideHaptic(): Haptic = Haptic(AppContext.application)
