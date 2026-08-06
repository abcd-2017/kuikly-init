package com.kuikly.init.common.base.platform.haptic

/** 震动冲击风格 */
enum class HapticStyle { LIGHT, MEDIUM, HEAVY }

/** 震动通知类型 */
enum class HapticNotification { SUCCESS, WARNING, FAILURE }

/**
 * 震动反馈能力抽象
 *
 * 提供冲击、通知、选择变更和停止震动功能。
 * - Android: 基于 VibratorManager / VibrationEffect
 * - iOS: 基于 UIImpactFeedbackGenerator / UINotificationFeedbackGenerator
 * - OHOS: 基于 @ohos.vibrator
 */
expect class Haptic {
    /**
     * 冲击反馈
     *
     * @param style 冲击强度（LIGHT / MEDIUM / HEAVY）
     */
    fun impact(style: HapticStyle = HapticStyle.MEDIUM)

    /**
     * 通知反馈
     *
     * @param type 通知类型（SUCCESS / WARNING / FAILURE）
     */
    fun notification(type: HapticNotification)

    /** 选择变更反馈（轻点） */
    fun selectionChanged()

    /** 停止当前震动 */
    fun stop()
}

/** 全局访问入口 */
expect fun provideHaptic(): Haptic
