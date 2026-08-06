package com.kuikly.init.common.base.platform.screen

import platform.UIKit.UIApplication
import platform.UIKit.UIScreen
import platform.Foundation.NSOperatingSystemVersion

/**
 * iOS 屏幕信息实现
 *
 * 基于 UIScreen.mainScreen 获取屏幕尺寸和密度。
 */
actual fun provideScreenInfo(): ScreenInfo {
    return try {
        val screen = UIScreen.mainScreen
        val bounds = screen.bounds
        val scale = screen.scale
        ScreenInfo(
            widthPx = (bounds.size.width * scale).toInt(),
            heightPx = (bounds.size.height * scale).toInt(),
            densityDpi = (scale * 160).toInt(), // iOS 以 160 为基准 DPI
            density = scale.toFloat(),
            scaledDensity = scale.toFloat(),
            rotation = getOrientation()
        )
    } catch (e: Exception) {
        ScreenInfo(
            widthPx = 0,
            heightPx = 0,
            densityDpi = 160,
            density = 1.0f,
            scaledDensity = 1.0f,
            rotation = 0
        )
    }
}

private fun getOrientation(): Int {
    return try {
        val orientation = UIApplication.sharedApplication.statusBarOrientation
        when (orientation) {
            platform.UIKit.UIInterfaceOrientationPortrait -> 0
            platform.UIKit.UIInterfaceOrientationLandscapeRight -> 90
            platform.UIKit.UIInterfaceOrientationPortraitUpsideDown -> 180
            platform.UIKit.UIInterfaceOrientationLandscapeLeft -> 270
            else -> 0
        }
    } catch (e: Exception) {
        0
    }
}
