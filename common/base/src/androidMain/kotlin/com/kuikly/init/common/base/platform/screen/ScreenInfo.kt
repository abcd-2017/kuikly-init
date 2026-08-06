package com.kuikly.init.common.base.platform.screen

import android.content.res.Configuration
import com.kuikly.init.common.base.platform.AppContext

/**
 * Android 屏幕信息实现
 *
 * 基于 Resources.displayMetrics 和 Configuration。
 */
actual fun provideScreenInfo(): ScreenInfo {
    return try {
        val context = AppContext.application
        val metrics = context.resources.displayMetrics
        val configuration = context.resources.configuration
        ScreenInfo(
            widthPx = metrics.widthPixels,
            heightPx = metrics.heightPixels,
            densityDpi = metrics.densityDpi,
            density = metrics.density,
            scaledDensity = metrics.scaledDensity,
            rotation = configuration.orientation
        )
    } catch (e: Exception) {
        // 兜底返回默认值
        ScreenInfo(
            widthPx = 0,
            heightPx = 0,
            densityDpi = 160,
            density = 1.0f,
            scaledDensity = 1.0f,
            rotation = Configuration.ORIENTATION_PORTRAIT
        )
    }
}
