package com.kuikly.init.common.base.platform.screen

/**
 * 屏幕信息
 *
 * 提供设备屏幕的尺寸、密度、方向等物理特性。
 *
 * @param widthPx 屏幕宽度（像素）
 * @param heightPx 屏幕高度（像素）
 * @param densityDpi DPI 值（如 160/320/480）
 * @param density 密度比例（如 1.0/2.0/3.0）
 * @param scaledDensity 字体缩放密度
 * @param rotation 屏幕旋转方向（0/90/180/270）
 */
data class ScreenInfo(
    val widthPx: Int,
    val heightPx: Int,
    val densityDpi: Int,
    val density: Float,
    val scaledDensity: Float,
    val rotation: Int
)

/** 全局访问入口 */
expect fun provideScreenInfo(): ScreenInfo
